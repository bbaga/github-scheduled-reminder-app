package com.bbaga.githubscheduledreminderapp.domain.configuration.sources;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;

import java.util.ArrayList;

public abstract class EventAsSourceConfig extends SourceConfig {
    private String xGithubEvent;

    public EventAsSourceConfig(String type, String xGithubEvent, ArrayList<AbstractFilterConfig> filters) {
        super(type, filters);
        setXGithubEvent(xGithubEvent);
    }

    public String getxGithubEvent() {
        return xGithubEvent;
    }

    public void setXGithubEvent(String xGithubEvent) {
        this.xGithubEvent = xGithubEvent;
    }

    @Override
    public Boolean hasEventAsSource() {
        return true;
    }
}
