package com.bbaga.githubscheduledreminderapp.domain.configuration.sources;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;

import java.util.ArrayList;

public abstract class RepositoryAsSourceConfig extends SourceConfig {
    public RepositoryAsSourceConfig(String type, ArrayList<AbstractFilterConfig> filters) {
        super(type, filters);
    }

    public RepositoryAsSourceConfig() {
        super();
    }

    public RepositoryAsSourceConfig(String type) {
        super(type, new ArrayList<>());
    }

    @Override
    public Boolean hasRepositoryAsSource() {
        return true;
    }
}
