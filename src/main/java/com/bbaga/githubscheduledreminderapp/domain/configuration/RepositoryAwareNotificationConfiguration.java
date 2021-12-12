package com.bbaga.githubscheduledreminderapp.domain.configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;

public class RepositoryAwareNotificationConfiguration extends NotificationConfiguration implements RepositoryAwareNotificationConfigurationInterface {

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type", defaultImpl = NotificationConfiguration.class)
    private Map<String, NotificationConfigurationInterface> repositories;

    public RepositoryAwareNotificationConfiguration() {}

    public Map<String, NotificationConfigurationInterface> getRepositories() {
        return repositories;
    }

    public void setRepositories(Map<String, NotificationConfigurationInterface> repositories) {
        this.repositories = repositories;
    }
}
