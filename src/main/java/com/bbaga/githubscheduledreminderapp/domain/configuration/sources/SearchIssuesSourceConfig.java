package com.bbaga.githubscheduledreminderapp.domain.configuration.sources;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;

import java.util.ArrayList;

public class SearchIssuesSourceConfig extends SearchAsSourceConfig {
    private String query = "";

    public SearchIssuesSourceConfig() {
        super(Sources.SEARCH_ISSUES.label);
    }

    public SearchIssuesSourceConfig(String type) {
        super(type, new ArrayList<>());
    }

    public SearchIssuesSourceConfig(String type, ArrayList<AbstractFilterConfig> filters) {
        super(type, filters);
    }

    public SearchIssuesSourceConfig(String type, String query) {
        super(type, new ArrayList<>());
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
