package com.bbaga.githubscheduledreminderapp.domain.configuration;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.Source;

import java.util.ArrayList;

public interface NotificationConfigurationInterface {
    ArrayList<Source> getSources();

    void setSources(ArrayList<Source> sources);
}
