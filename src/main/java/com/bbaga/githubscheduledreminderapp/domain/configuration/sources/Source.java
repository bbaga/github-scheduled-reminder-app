package com.bbaga.githubscheduledreminderapp.domain.configuration.sources;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Source {
    protected String type;

    protected ArrayList<AbstractFilter> filters;

    public Source(String type, ArrayList<AbstractFilter> filters) {
        this.type = type;
        this.filters = filters;
    }

    public Source() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<AbstractFilter> getFilters() {
        return filters;
    }

    public void setFilters(ArrayList<AbstractFilter> filters) {
        this.filters = filters;
    }

    public Boolean isRepositoryAsSource() {
        return false;
    }

    public Boolean isSearchAsSource() {
        return false;
    }
}
