package com.bbaga.githubscheduledreminderapp.domain.configuration.sources;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RepositoryIssuesSource extends RepositoryAsSource {
    public RepositoryIssuesSource() {
        super(Sources.REPOSITORY_ISSUES.label);
    }

    public RepositoryIssuesSource(String type) {
        super(type, new ArrayList<>());
    }

    public RepositoryIssuesSource(String type, ArrayList<AbstractFilter> filters) {
        super(type, filters);

    }
}
