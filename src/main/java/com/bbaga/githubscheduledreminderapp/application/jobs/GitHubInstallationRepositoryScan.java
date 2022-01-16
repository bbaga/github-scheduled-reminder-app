package com.bbaga.githubscheduledreminderapp.application.jobs;

import com.bbaga.githubscheduledreminderapp.domain.configuration.ConfigGraphNode;
import com.bbaga.githubscheduledreminderapp.domain.configuration.configGraphUpdater.ConfigGraphUpdater;
import com.bbaga.githubscheduledreminderapp.domain.configuration.NotificationInterface;
import com.bbaga.githubscheduledreminderapp.infrastructure.configuration.InRepoConfig;
import com.bbaga.githubscheduledreminderapp.infrastructure.configuration.InRepoConfigParser;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubAppInstallation;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubBuilderFactory;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.repositories.GitHubInstallationRepository;
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
@DisallowConcurrentExecution
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

        GitHubAppInstallation installation = installationRepository.get(
            context.getJobDetail().getJobDataMap().getLong("installationId")
        );

        try {
            GHAppInstallationToken token = installation.unwrap().createToken().create();
            GitHub githubAuthAsInst = gitHubBuilderFactory.create()
                    .withAppInstallationToken(token.getToken())
                    .build();

            for(GHRepository repo : GitHubClientUtil.listRepositories(githubAuthAsInst)) {
                try {
                    InRepoConfig inRepoConfig = inRepoConfigParser.getFrom(repo);

                    if (repo.isArchived() || !inRepoConfig.getEnabled()) {
                        continue;
                    }

                    for (NotificationInterface notification : inRepoConfig.getNotifications()) {
                        configGraphUpdater.updateEntry(notification, installationId, repo.getFullName(), currentRunStamp);
                    }
                } catch (GHFileNotFoundException e) {
                    logger.debug("No config file in {}", repo.getFullName());
                }
            }

            // Remove entries if they didn't appear in this round
            configGraphUpdater.clearOutdated(installationId, currentRunStamp);

            Scheduler scheduler = context.getScheduler();
            Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals("notifications"));

            // Unschedule notifications that don't exist anymore
            for (TriggerKey triggerKey : triggerKeys) {
                if (!configGraph.containsKey(triggerKey.getName())) {
                    JobKey jobKey = new JobKey(triggerKey.getName());
                    JobDetail job = scheduler.getJobDetail(jobKey);
                    if (job != null) {
                        scheduler.deleteJob(jobKey);
                    }
                }
            }
        } catch (IOException | SchedulerException e) {
            e.printStackTrace();
        }
        logger.info("Repository scanning for Installation {} is complete", installationId);
    }
}
