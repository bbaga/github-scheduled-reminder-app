package com.bbaga.githubscheduledreminderapp.notifications.slack;

import com.bbaga.githubscheduledreminderapp.GitHubAppInstallationService;
import com.bbaga.githubscheduledreminderapp.configuration.ConfigGraphNode;
import com.bbaga.githubscheduledreminderapp.configuration.Notification;
import com.bbaga.githubscheduledreminderapp.configuration.configgraphnode.RepositoryRecord;
import com.bbaga.githubscheduledreminderapp.notifications.NotificationDataProvider;
import org.kohsuke.github.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelNotificationDataProvider implements NotificationDataProvider<ChannelNotificationDataProvider.Data> {

    private final ConfigGraphNode config;
    private final GitHubAppInstallationService appInstallationService;

    public ChannelNotificationDataProvider(ConfigGraphNode config, GitHubAppInstallationService appInstallationService) {
        this.config = config;
        this.appInstallationService = appInstallationService;
    }

    public Data getData() {

        Notification notification = this.config.getNotification();
        ConcurrentHashMap<Integer, RepositoryRecord> repositories = this.config.getRepositories();

        GitHub client;
        Set<GHIssue> issues = new HashSet<>();
        Set<GHIssue> pullRequests = new HashSet<>();
        for (RepositoryRecord repository : repositories.values()) {
            client = appInstallationService.getClientByInstallationId(repository.getInstallationId());
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

        return new Data(notification, issues, pullRequests);
    }

    public static class Data {
        private final Notification notification;
        private final Set<GHIssue> issues;
        private final Set<GHIssue> pullRequests;

        public Data(Notification notification, Set<GHIssue> issues, Set<GHIssue> pullRequests) {
            this.notification = notification;
            this.issues = issues;
            this.pullRequests = pullRequests;
        }

        public Notification getNotification() {
            return notification;
        }

        public Set<GHIssue> getIssues() {
            return issues;
        }

        public Set<GHIssue> getPullRequests() {
            return pullRequests;
        }
    }
}
