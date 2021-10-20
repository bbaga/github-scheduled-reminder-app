package com.bbaga.githubscheduledreminderapp.jobs;

import com.bbaga.githubscheduledreminderapp.configuration.ConfigGraphNode;
import com.bbaga.githubscheduledreminderapp.configuration.Extending;
import com.bbaga.githubscheduledreminderapp.configuration.InRepoConfig;
import com.bbaga.githubscheduledreminderapp.configuration.Notification;
import com.bbaga.githubscheduledreminderapp.configuration.configgraphnode.RepositoryRecord;
import com.bbaga.githubscheduledreminderapp.repositories.GitHubInstallationRepository;
import org.kohsuke.github.*;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GitHubInstallationRepositoryScan implements Job {

    @Autowired
    private GitHubInstallationRepository installationRepository;

    @Autowired
    @Qualifier("ConfigGraph")
    private ConcurrentHashMap<String, ConfigGraphNode> configGraph;

    private final Logger logger = LoggerFactory.getLogger(GitHubInstallationRepositoryScan.class);

    public void execute(JobExecutionContext context) {
        Long installationId = context.getJobDetail().getJobDataMap().getLong("installationId");
        Instant currentRunStamp = Instant.now();

        logger.info("Starting Repository scanning for Installation {}", installationId);

        GHAppInstallation installation = installationRepository.get(
            context.getJobDetail().getJobDataMap().getLong("installationId")
        );

        try {
            GHAppInstallationToken token = installation.createToken().create();
            GitHub githubAuthAsInst = new GitHubBuilder()
                    .withAppInstallationToken(token.getToken())
                    .build();

            // Hack to get around bug in the client
            GitHub tempGH = GitHubClientUtil.getRoot(installation);
            GitHubClientUtil.setRoot(installation, githubAuthAsInst);

            for(GHRepository repo : installation.listRepositories()) {

                // Hack to get around bug in the client
                GitHubClientUtil.setRoot(installation, tempGH);

                try {
                    GHContent content = repo.getFileContent(".demo-bot.yaml");
                    InRepoConfig inRepoConfig = new Yaml().loadAs(new String(content.read().readAllBytes(), StandardCharsets.UTF_8), InRepoConfig.class);

                    if (repo.isArchived() || !inRepoConfig.getEnabled()) {
                        // Hack to get around bug in the client
                        GitHubClientUtil.setRoot(installation, githubAuthAsInst);
                        continue;
                    }

                    for (Notification notification : inRepoConfig.getNotifications()) {
                        if (notification.getSchedule() != null) {
                            notification.setName(getNotificationKey(repo.getFullName(), notification.getName()));
                            String notificationKey = notification.getName();

                            if (!configGraph.containsKey(notificationKey)) {
                                configGraph.put(notificationKey, new ConfigGraphNode(installationId, notification, currentRunStamp));
                            } else {
                                configGraph.get(notificationKey).setSeenAt(currentRunStamp);
                            }
                        } else {
                            Extending extending = notification.getExtending();

                            if (extending.getRepository() == null || extending.getName() == null) {
                                continue;
                            }

                            String notificationKey = getNotificationKey(extending);
                            if (configGraph.containsKey(notificationKey)) {
                                configGraph.get(notificationKey).putRepository(new RepositoryRecord(repo.getFullName(), installationId, currentRunStamp));
                            }
                        }
                    }
                } catch (GHFileNotFoundException e) {
                    logger.debug("No config file in {}", repo.getFullName());
                }

                // Hack to get around bug in the client
                GitHubClientUtil.setRoot(installation, githubAuthAsInst);
            }

            GitHubClientUtil.setRoot(installation, tempGH);

            // Remove entries if they didn't appear
            configGraph.entrySet().removeIf(entry -> {
                ConfigGraphNode node = entry.getValue();
                if (node.getInstallationId().equals(installationId) && node.getLastSeenAt() != currentRunStamp) {
                    return true;
                }

                node.getRepositories().entrySet().removeIf(repoEntry -> {
                    RepositoryRecord repositoryRecord = repoEntry.getValue();
                    return repositoryRecord.getInstallationId().equals(installationId)
                            && repositoryRecord.getSeenAt() != currentRunStamp;
                });

                return false;
            });

            Scheduler scheduler = context.getScheduler();
            Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals("notifications"));

            // Unschedule notifications that don't exist anymore
            for (TriggerKey triggerKey : triggerKeys) {
                if (!configGraph.containsKey(triggerKey.getName())) {
                    scheduler.unscheduleJob(triggerKey);

                    JobKey jobKey = new JobKey(triggerKey.getName());
                    JobDetail job = scheduler.getJobDetail(jobKey);
                    if (job != null) {
                        scheduler.deleteJob(jobKey);
                    }
                } else {
                    configGraph.remove(triggerKey.getName());
                }
            }

            configGraph.forEach((String notificationKey, ConfigGraphNode node) -> {
                JobKey jobKey = new JobKey(notificationKey);
                JobDetail job;
                Trigger trigger;

                try {
                    if (scheduler.getJobDetail(jobKey) == null) {
                        job = JobBuilder.newJob(ScheduledNotification.class)
                                .withIdentity(jobKey)
                                .usingJobData("repositories", node.getRepositories().toString())
                                .storeDurably(true)
                                .build();

                        trigger = TriggerBuilder.newTrigger()
                                .withSchedule(CronScheduleBuilder.cronSchedule(node.getNotification().getSchedule()))
                                .withIdentity(jobKey.getName()).build();

                        scheduler.scheduleJob(job, trigger);
                    }
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException | SchedulerException e) {
            e.printStackTrace();
        }
        logger.info("Repository scanning for Installation {} is complete", installationId);
    }

    private String getNotificationKey(Extending extending) {
        return getNotificationKey(extending.getRepository(), extending.getName());
    }

    private String getNotificationKey(String repository, String name) {
        return String.format("%s-%s", repository, name);
    }
}
