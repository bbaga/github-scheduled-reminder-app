package com.bbaga.githubscheduledreminderapp.domain.notifications;

import com.bbaga.githubscheduledreminderapp.domain.configuration.ConfigGraphNode;

public interface NotificationStrategyInterface {
    void sendNotification(ConfigGraphNode node);
}
