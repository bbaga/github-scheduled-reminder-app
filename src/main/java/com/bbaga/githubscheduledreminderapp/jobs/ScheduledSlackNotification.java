package com.bbaga.githubscheduledreminderapp.jobs;

import com.bbaga.githubscheduledreminderapp.configuration.ConfigGraphNode;
import com.bbaga.githubscheduledreminderapp.configuration.Notification;
import com.bbaga.githubscheduledreminderapp.configuration.configgraphnode.RepositoryRecord;
import com.bbaga.githubscheduledreminderapp.notifications.slack.ChannelNotification;
import com.bbaga.githubscheduledreminderapp.repositories.GitHubInstallationRepository;
import org.kohsuke.github.*;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ScheduledSlackNotification implements Job {

    @Autowired
    private ChannelNotification channelNotification;

    @Autowired
    private GitHubInstallationRepository installationRepository;

    @Autowired
    @Qualifier("ConfigGraph")
    private ConcurrentHashMap<String, ConfigGraphNode> configGraph;

    private final Logger logger = LoggerFactory.getLogger(ScheduledSlackNotification.class);
    private final HashMap<Long, GitHub> installationClients = new HashMap<>();

    public void execute(JobExecutionContext context) throws JobExecutionException {
        String jobName = context.getJobDetail().getKey().getName();
        logger.info("Starting notification job for {}", jobName);

        Notification notification = configGraph.get(jobName).getNotification();
        ConcurrentHashMap<Integer, RepositoryRecord> repositories = configGraph.get(jobName).getRepositories();

        GitHub client;
        Set<GHIssue> issues = new HashSet<>();
        Set<GHIssue> pullRequests = new HashSet<>();
        for (RepositoryRecord repository : repositories.values()) {
            client = grabInstallationClient(repository.getInstallationId());
            try {
                client.getRepository(repository.getRepository()).getIssues(GHIssueState.OPEN).forEach((GHIssue issue) -> {
                    if (issue.isPullRequest()) {
                        pullRequests.add(issue);
                    } else {
                        issues.add(issue);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        channelNotification.send(notification,issues, pullRequests);

        logger.info("Finished notification job for {}", jobName);
    }



    private GitHub grabInstallationClient(Long installationId) {
        if (!installationClients.containsKey(installationId)) {
            GHAppInstallationToken token;
            GitHub installationClient;
            try {
                token = installationRepository.get(installationId).createToken().create();
                installationClient = new GitHubBuilder()
                        .withAppInstallationToken(token.getToken())
                        .build();
            } catch (IOException e) {
                throw new RuntimeException(String.format("Could not crate client for installation %d", installationId));
            }

            installationClients.put(installationId, installationClient);

            return installationClient;
        }

        return installationClients.get(installationId);
    }
}
