package com.bbaga.githubscheduledreminderapp.domain.configuration.sources;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilter;

import java.util.ArrayList;

public abstract class RepositoryAsSource extends Source {
    public RepositoryAsSource(String type, ArrayList<AbstractFilter> filters) {
        super(type, filters);
    }

    public RepositoryAsSource() {
        super();
    }

    public RepositoryAsSource(String type) {
        super(type, new ArrayList<>());
    }

    @Override
    public Boolean isRepositoryAsSource() {
        return true;
    }
}
