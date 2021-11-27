package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.bbaga.githubscheduledreminderapp.domain.GitHubAppInstallationService;
import com.bbaga.githubscheduledreminderapp.domain.configuration.Notification;
import com.bbaga.githubscheduledreminderapp.domain.configuration.RepositoryRecord;
import com.bbaga.githubscheduledreminderapp.domain.notifications.NotificationDataProviderInterface;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.RepositoryIssuesSource;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.RepositoryPRsSource;
import org.kohsuke.github.*;
import java.io.IOException;
import java.util.ArrayList;
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
        ArrayList<GHIssue> issues = new ArrayList<>();
        for (RepositoryRecord repository : repositories.values()) {
            client = appInstallationService.getClientByInstallationId(repository.getInstallationId());
            try {
                GHRepository repo = client.getRepository(repository.getRepository());
                issues.addAll(RepositoryIssuesSource.get(repo));
                issues.addAll(RepositoryPRsSource.get(repo));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        issues.sort((GHIssue issueA, GHIssue issueB) -> {
            try {
                return issueA.getCreatedAt().compareTo(issueB.getCreatedAt());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return 0;
        });

        return new Data(notification, issues);
    }

    public static class Data {
        private final Notification notification;
        private final ArrayList<GHIssue> issues;

        public Data(Notification notification, ArrayList<GHIssue> issues) {
            this.notification = notification;
            this.issues = issues;
        }

        public Notification getNotification() {
            return notification;
        }

        public ArrayList<GHIssue> getIssues() {
            return issues;
        }
    }
}
