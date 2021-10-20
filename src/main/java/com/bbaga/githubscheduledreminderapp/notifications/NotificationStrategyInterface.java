package com.bbaga.githubscheduledreminderapp.notifications;

import com.bbaga.githubscheduledreminderapp.configuration.ConfigGraphNode;

public interface NotificationStrategyInterface {
    void sendNotification(ConfigGraphNode node);
}
