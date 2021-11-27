package com.bbaga.githubscheduledreminderapp.domain.configuration.sources;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilter;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.DraftFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Source {
    private String type;

    @JsonSubTypes({
        @JsonSubTypes.Type(value = DraftFilter.class, name = "draft-filter"),
    })
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
    private ArrayList<AbstractFilter> filters;

    public Source() {}

    public Source(String type, ArrayList<AbstractFilter> filters) {
        this.type = type;
        this.filters = filters;
    }

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
}
