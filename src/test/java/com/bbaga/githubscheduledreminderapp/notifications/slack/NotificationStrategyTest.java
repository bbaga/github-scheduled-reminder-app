package com.bbaga.githubscheduledreminderapp.notifications.slack;

import com.bbaga.githubscheduledreminderapp.GitHubAppInstallationService;
import com.bbaga.githubscheduledreminderapp.configuration.ConfigGraphNode;
import com.bbaga.githubscheduledreminderapp.configuration.Notification;
import com.bbaga.githubscheduledreminderapp.configuration.configgraphnode.RepositoryRecord;
import com.bbaga.githubscheduledreminderapp.notifications.NotificationStrategyInterface;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.concurrent.ConcurrentHashMap;

class NotificationStrategyTest {

    @Test
    void create() {
        NotificationStrategy.create(
            Mockito.mock(GitHubAppInstallationService.class),
            Mockito.mock(ChannelNotification.class)
        );
    }

    @Test
    void sendNotification() {
        GitHubAppInstallationService appInstallationService = Mockito.mock(GitHubAppInstallationService.class);
        ChannelNotification channelNotification = Mockito.mock(ChannelNotification.class);
        ChannelNotificationDataProvider.Data data = Mockito.mock(ChannelNotificationDataProvider.Data.class);
        ChannelNotificationDataProvider notificationDataProvider = Mockito.mock(ChannelNotificationDataProvider.class);

        //Config node mock
        ConcurrentHashMap<Integer, RepositoryRecord> hashMap = new ConcurrentHashMap<>();
        Notification notification = Mockito.mock(Notification.class);
        Mockito.when(notificationDataProvider.getData()).thenReturn(data);
        ConfigGraphNode node = Mockito.mock(ConfigGraphNode.class);
        Mockito.when(node.getNotification()).thenReturn(notification);
        Mockito.when(node.getRepositories()).thenReturn(hashMap);

        // Channel notification static factory mock
        MockedStatic<ChannelNotificationDataProvider> provider = Mockito.mockStatic(ChannelNotificationDataProvider.class);
        provider.when(() -> ChannelNotificationDataProvider.create(appInstallationService, notification, hashMap))
            .thenReturn(notificationDataProvider);

        NotificationStrategyInterface strategy = NotificationStrategy.create(appInstallationService, channelNotification);
        strategy.sendNotification(node);

        Mockito.verify(notificationDataProvider, Mockito.times(1)).getData();
        Mockito.verify(channelNotification, Mockito.times(1)).send(data);
    }
}