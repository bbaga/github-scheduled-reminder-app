package com.bbaga.githubscheduledreminderapp.domain.configuration;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.Source;

import java.util.ArrayList;

public class NotificationConfiguration implements NotificationConfigurationInterface {
    private ArrayList<Source> sources = new ArrayList<>();

    public NotificationConfiguration() {}

    @Override
    public ArrayList<Source> getSources() {
        return sources.size() == 0 ? NotificationConfiguration.getDefaultSources() : sources;
    }

    @Override
    public void setSources(ArrayList<Source> sources) {
        this.sources = sources;
    }

    public static ArrayList<Source> getDefaultSources() {
        ArrayList<Source> sources = new ArrayList<>();
        sources.add(new Source("issues", new ArrayList<>()));
        sources.add(new Source("pull-requests", new ArrayList<>()));

        return sources;
    }
}
