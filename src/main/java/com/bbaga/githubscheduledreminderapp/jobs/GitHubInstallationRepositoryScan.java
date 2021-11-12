package com.bbaga.githubscheduledreminderapp.jobs;

import com.bbaga.githubscheduledreminderapp.configuration.*;
import com.bbaga.githubscheduledreminderapp.infrastructure.GitHub.GitHubBuilderFactory;
import com.bbaga.githubscheduledreminderapp.repositories.GitHubInstallationRepository;
import org.kohsuke.github.*;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GitHubInstallationRepositoryScan implements Job {

    private final ConfigGraphUpdater configGraphUpdater;
    private final GitHubBuilderFactory gitHubBuilderFactory;
    private final GitHubInstallationRepository installationRepository;

    private final ConcurrentHashMap<String, ConfigGraphNode> configGraph;

    private final Logger logger = LoggerFactory.getLogger(GitHubInstallationRepositoryScan.class);
    private final InRepoConfigParser inRepoConfigParser;

    @Autowired
    GitHubInstallationRepositoryScan(
        GitHubBuilderFactory gitHubBuilderFactory,
        GitHubInstallationRepository installationRepository,
        @Qualifier("ConfigGraph") ConcurrentHashMap<String, ConfigGraphNode> configGraph,
        ConfigGraphUpdater configGraphUpdater,
        InRepoConfigParser inRepoConfigParser
    ) {
        this.gitHubBuilderFactory = gitHubBuilderFactory;
        this.installationRepository = installationRepository;
        this.configGraph = configGraph;
        this.configGraphUpdater = configGraphUpdater;
        this.inRepoConfigParser = inRepoConfigParser;
    }

    public void execute(JobExecutionContext context) {
        long installationId = context.getJobDetail().getJobDataMap().getLong("installationId");
        Instant currentRunStamp = Instant.now();

        logger.info("Starting Repository scanning for Installation {}", installationId);

        GHAppInstallation installation = installationRepository.get(
            context.getJobDetail().getJobDataMap().getLong("installationId")
        );

        try {
            GHAppInstallationToken token = installation.createToken().create();
            GitHub githubAuthAsInst = gitHubBuilderFactory.create()
                    .withAppInstallationToken(token.getToken())
                    .build();

            // Hack to get around bug in the client
            GitHub tempGH = GitHubClientUtil.getRoot(installation);
            GitHubClientUtil.setRoot(installation, githubAuthAsInst);

            for(GHRepository repo : installation.listRepositories()) {

                // Hack to get around bug in the client
                GitHubClientUtil.setRoot(installation, tempGH);

                try {
                    InRepoConfig inRepoConfig = inRepoConfigParser.getFrom(repo);

                    if (repo.isArchived() || !inRepoConfig.getEnabled()) {
                        // Hack to get around bug in the client
                        GitHubClientUtil.setRoot(installation, githubAuthAsInst);
                        continue;
                    }

                    for (Notification notification : inRepoConfig.getNotifications()) {
                        configGraphUpdater.updateEntry(notification, installationId, repo.getFullName(), currentRunStamp);
                    }
                } catch (GHFileNotFoundException e) {
                    logger.debug("No config file in {}", repo.getFullName());
                }

                // Hack to get around bug in the client
                GitHubClientUtil.setRoot(installation, githubAuthAsInst);
            }

            GitHubClientUtil.setRoot(installation, tempGH);

            // Remove entries if they didn't appear in this round
            configGraphUpdater.clearOutdated(installationId, currentRunStamp);

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
//                    configGraph.remove(triggerKey.getName());
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
}
