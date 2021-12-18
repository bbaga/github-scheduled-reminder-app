package com.bbaga.githubscheduledreminderapp.domain.configuration;

import com.bbaga.githubscheduledreminderapp.domain.configuration.configGraphUpdater.ConfigGraphUpdater;
import com.bbaga.githubscheduledreminderapp.domain.configuration.configGraphUpdater.NotificationConfigVisitor;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.SourceConfig;

import java.util.ArrayList;
import java.util.HashMap;

public class SlackRealtimeUserNotificationConfiguration implements NotificationConfigurationInterface {
    private final NotificationConfigurationInterface notificationConfiguration;

    private HashMap<String, String> userNameMap = new HashMap<>();

    public SlackRealtimeUserNotificationConfiguration() {
        notificationConfiguration = new NotificationConfiguration();
    }

    public HashMap<String, String> getUserNameMap() {
        return userNameMap;
    }

    public void setUserNameMap(HashMap<String, String> userNameMap) {
        this.userNameMap = userNameMap;
    }

    @Override
    public ArrayList<SourceConfig> getSources() {
        return notificationConfiguration.getSources();
    }

    @Override
    public void setSources(ArrayList<SourceConfig> sourceConfigs) {
        notificationConfiguration.setSources(sourceConfigs);
    }

    @Override
    public void accept(NotificationConfigVisitor configVisitor) {
        configVisitor.visit(this);
    }
}
