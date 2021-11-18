package com.bbaga.githubscheduledreminderapp.notifications.slack;

import com.bbaga.githubscheduledreminderapp.GitHubAppInstallationService;
import com.bbaga.githubscheduledreminderapp.configuration.Notification;
import com.bbaga.githubscheduledreminderapp.configuration.configgraphnode.RepositoryRecord;
import com.bbaga.githubscheduledreminderapp.notifications.NotificationDataProviderInterface;
import org.kohsuke.github.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelNotificationDataProvider implements NotificationDataProviderInterface<ChannelNotificationDataProvider.Data> {

    private final Notification notification;
    private final ConcurrentHashMap<Integer, RepositoryRecord> repositories;
    private final GitHubAppInstallationService appInstallationService;

    private ChannelNotificationDataProvider(
        GitHubAppInstallationService appInstallationService,
        Notification notification,
        ConcurrentHashMap<Integer, RepositoryRecord> repositories
    ) {
        this.appInstallationService = appInstallationService;
        this.notification = notification;
        this.repositories = repositories;
    }

    public static ChannelNotificationDataProvider create(
        GitHubAppInstallationService appInstallationService,
        Notification notification,
        ConcurrentHashMap<Integer, RepositoryRecord> repositories
    ) {
        return new ChannelNotificationDataProvider(appInstallationService, notification, repositories);
    }

    public Data getData() {

        GitHub client;
        Set<GHIssue> issues = new HashSet<>();
        Set<GHIssue> pullRequests = new HashSet<>();
        for (RepositoryRecord repository : repositories.values()) {
            client = appInstallationService.getClientByInstallationId(repository.getInstallationId());
            try {
                client.getRepository(repository.getRepository()).getIssues(GHIssueState.OPEN).forEach((GHIssue issue) -> {
                    if (!issue.isPullRequest()) {
                        issues.add(issue);
                    }
                });

                client.getRepository(repository.getRepository()).getPullRequests(GHIssueState.OPEN).forEach((GHPullRequest pr) -> {
                    try {
                        if (!pr.isDraft()) {
                            pullRequests.add(pr);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
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
