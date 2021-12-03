package com.bbaga.githubscheduledreminderapp.domain.configuration.sources;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RepositoryPRsSourceConfig extends RepositoryAsSourceConfig {
    public RepositoryPRsSourceConfig() {
        super(Sources.REPOSITORY_PRS.label);
    }

    public RepositoryPRsSourceConfig(String type) {
        super(type, new ArrayList<>());
    }

    public RepositoryPRsSourceConfig(String type, ArrayList<AbstractFilterConfig> filters) {
        super(type, filters);
    }
}
