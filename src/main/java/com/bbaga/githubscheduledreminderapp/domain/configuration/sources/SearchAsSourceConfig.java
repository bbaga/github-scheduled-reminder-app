package com.bbaga.githubscheduledreminderapp.domain.configuration.sources;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;

import java.util.ArrayList;

public abstract class SearchAsSourceConfig extends SourceConfig {
    public SearchAsSourceConfig(String type, ArrayList<AbstractFilterConfig> filters) {
        super(type, filters);
    }

    public SearchAsSourceConfig() {
        super();
    }

    public SearchAsSourceConfig(String type) {
        super(type, new ArrayList<>());
    }

    @Override
    public Boolean hasSearchAsSource() {
        return true;
    }
}
