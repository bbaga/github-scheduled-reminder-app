package com.bbaga.githubscheduledreminderapp.domain.configuration;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.*;
import java.util.ArrayList;

public class NotificationConfiguration implements NotificationConfigurationInterface {
    protected ArrayList<SourceConfig> sources = new ArrayList<>();

    public NotificationConfiguration() {}

    @Override
    public ArrayList<SourceConfig> getSources() {
        return sources;
    }

    @Override
    public void setSources(ArrayList<SourceConfig> sourceConfigs) {
        this.sources = sourceConfigs;
    }
}
