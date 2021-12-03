package com.bbaga.githubscheduledreminderapp.domain.configuration.sources;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.DraftFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.LabelFilterConfig;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;

import static com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.Filters.Constants.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class SourceConfig {
    protected String type;

    @JsonSubTypes({
            @JsonSubTypes.Type(value = DraftFilterConfig.class, name = DRAFT_FILTER),
            @JsonSubTypes.Type(value = LabelFilterConfig.class, name = LABEL_FILTER),
    })
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
    protected ArrayList<AbstractFilterConfig> filters;

    public SourceConfig(String type, ArrayList<AbstractFilterConfig> filters) {
        this.type = type;
        this.filters = filters;
    }

    public SourceConfig() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<AbstractFilterConfig> getFilters() {
        return filters;
    }

    public void setFilters(ArrayList<AbstractFilterConfig> filters) {
        this.filters = filters;
    }

    public Boolean isRepositoryAsSource() {
        return false;
    }

    public Boolean isSearchAsSource() {
        return false;
    }
}
