package com.bbaga.githubscheduledreminderapp.domain.configuration;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.SourceConfig;

import java.util.ArrayList;

public interface NotificationConfigurationInterface {
    ArrayList<SourceConfig> getSources();

    void setSources(ArrayList<SourceConfig> sourceConfigs);
}
