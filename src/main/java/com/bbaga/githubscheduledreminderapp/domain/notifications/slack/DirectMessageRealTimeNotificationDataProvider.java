package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.bbaga.githubscheduledreminderapp.domain.configuration.Notification;
import com.bbaga.githubscheduledreminderapp.domain.notifications.NotificationDataProviderInterface;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;

public class DirectMessageRealTimeNotificationDataProvider implements NotificationDataProviderInterface<DirectMessageRealTimeNotificationDataProvider.Data> {

    private final Notification notification;
    private final GitHubIssue gitHubIssue;

    private DirectMessageRealTimeNotificationDataProvider(
        Notification notification,
        GitHubIssue gitHubIssue
    ) {
        this.notification = notification;
        this.gitHubIssue = gitHubIssue;
    }

    public Data getData() {
        return new Data(notification, this.gitHubIssue);
    }

    public static class Data {
        private final Notification notification;
        private GitHubIssue issue;

        public Data(Notification notification, GitHubIssue issue) {
            this.notification = notification;
            this.issue = issue;
        }

        public Notification getNotification() {
            return notification;
        }

        public GitHubIssue getIssue() {
            return issue;
        }
    }
}
