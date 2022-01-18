package com.bbaga.githubscheduledreminderapp.domain.configuration;

import com.bbaga.githubscheduledreminderapp.domain.configuration.configGraphUpdater.NotificationConfigVisitor;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.SourceConfig;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.Map;

public class RepositoryAwareNotificationConfiguration implements RepositoryAwareNotificationConfigurationInterface, NotificationConfigurationInterface {

    private final NotificationConfigurationInterface notificationConfiguration;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type", defaultImpl = NotificationConfiguration.class)
    private Map<String, NotificationConfigurationInterface> repositories;

    public RepositoryAwareNotificationConfiguration() {
        notificationConfiguration = new NotificationConfiguration();
    }

    public Map<String, NotificationConfigurationInterface> getRepositories() {
        return repositories;
    }

    public void setRepositories(Map<String, NotificationConfigurationInterface> repositories) {
        this.repositories = repositories;
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
