package com.bbaga.githubscheduledreminderapp.domain.configuration.sources;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilter;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.DraftFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;

import static com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.Filters.Constants.DRAFT_FILTER;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RepositoryPRsSource extends RepositoryAsSource {

    @JsonSubTypes({
        @JsonSubTypes.Type(value = DraftFilter.class, name = DRAFT_FILTER),
    })
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
    private ArrayList<AbstractFilter> filters;

    public RepositoryPRsSource() {
        super(Sources.REPOSITORY_PRS.label);
    }

    public RepositoryPRsSource(String type) {
        super(type, new ArrayList<>());
    }

    public RepositoryPRsSource(String type, ArrayList<AbstractFilter> filters) {
        super(type, filters);
    }
}
