package com.bbaga.githubscheduledreminderapp.domain.configuration.sources;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

public class SearchIssuesSource extends SearchAsSource {
    private String query = "";

    public SearchIssuesSource() {
        super(Sources.SEARCH_ISSUES.label);
    }

    public SearchIssuesSource(String type) {
        super(type, new ArrayList<>());
    }

    public SearchIssuesSource(String type, ArrayList<AbstractFilter> filters) {
        super(type, filters);
    }

    public SearchIssuesSource(String type, String query) {
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
