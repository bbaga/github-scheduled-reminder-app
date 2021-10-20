package com.bbaga.githubscheduledreminderapp.notifications.slack;

import com.bbaga.githubscheduledreminderapp.GitHubAppInstallationService;
import com.bbaga.githubscheduledreminderapp.configuration.ConfigGraphNode;
import com.bbaga.githubscheduledreminderapp.notifications.NotificationStrategyInterface;

public class NotificationStrategy implements NotificationStrategyInterface {
    private final GitHubAppInstallationService appInstallationService;
    private final ChannelNotification notificationService;

    private NotificationStrategy(GitHubAppInstallationService appInstallationService, ChannelNotification notificationService) {
        this.appInstallationService = appInstallationService;
        this.notificationService = notificationService;
    }

    public static NotificationStrategy create(GitHubAppInstallationService appInstallationService, ChannelNotification notificationService) {
        return new NotificationStrategy(appInstallationService, notificationService);
    }

    @Override
    public void sendNotification(ConfigGraphNode node) {
        notificationService.send(getDataProvider(node).getData());
    }

    private ChannelNotificationDataProvider getDataProvider(ConfigGraphNode node) {
        return ChannelNotificationDataProvider.create(
                appInstallationService,
                node.getNotification(),
                node.getRepositories()
        );
    }
}
