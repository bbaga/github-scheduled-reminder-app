package com.bbaga.githubscheduledreminderapp.domain.configuration;

import java.util.Map;

public interface RepositoryAwareNotificationConfigurationInterface {
    Map<String, NotificationConfigurationInterface> getRepositories();

    void setRepositories(Map<String, NotificationConfigurationInterface> repositories);
}
