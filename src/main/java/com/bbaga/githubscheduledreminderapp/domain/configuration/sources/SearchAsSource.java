package com.bbaga.githubscheduledreminderapp.domain.configuration.sources;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilter;

import java.util.ArrayList;

public abstract class SearchAsSource extends Source {
    public SearchAsSource(String type, ArrayList<AbstractFilter> filters) {
        super(type, filters);
    }

    public SearchAsSource() {
        super();
    }

    public SearchAsSource(String type) {
        super(type, new ArrayList<>());
    }

    @Override
    public Boolean isSearchAsSource() {
        return true;
    }
}
