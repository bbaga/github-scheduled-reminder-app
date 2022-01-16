package com.bbaga.githubscheduledreminderapp.domain.configuration;

import com.bbaga.githubscheduledreminderapp.domain.configuration.configGraphUpdater.NotificationConfigVisitor;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.SourceConfig;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;

public class SlackRealTimeUserNotificationConfiguration implements NotificationConfigurationInterface {
    private final NotificationConfigurationInterface notificationConfiguration;

    @JsonProperty("user-name-map")
    private HashMap<String, String> userNameMap = new HashMap<>();

    public SlackRealTimeUserNotificationConfiguration() {
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
