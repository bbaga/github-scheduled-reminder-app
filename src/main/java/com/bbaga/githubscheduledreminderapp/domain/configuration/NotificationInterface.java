package com.bbaga.githubscheduledreminderapp.domain.configuration;

import com.bbaga.githubscheduledreminderapp.domain.configuration.configGraphUpdater.NotificationVisitor;

public interface NotificationInterface {
    NotificationConfigurationInterface getConfig();

    void setConfig(NotificationConfigurationInterface config);
    void accept(NotificationVisitor visitor);
}
