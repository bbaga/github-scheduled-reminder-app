package com.bbaga.githubscheduledreminderapp.domain.configuration.sources;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RepositoryIssuesSourceConfig extends RepositoryAsSourceConfig {
    public RepositoryIssuesSourceConfig() {
        super(Sources.REPOSITORY_ISSUES.label);
    }

    public RepositoryIssuesSourceConfig(String type) {
        super(type, new ArrayList<>());
    }

    public RepositoryIssuesSourceConfig(String type, ArrayList<AbstractFilterConfig> filters) {
        super(type, filters);

    }
}
