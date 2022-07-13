package com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PathFilterConfig extends AbstractFilterConfig {
    @JsonProperty("include")
    private List<String> includePaths;

    public PathFilterConfig() {
        super(Filters.PATH_FILTER.label);
        includePaths = new ArrayList<>();
    }

    public List<String> getIncludePaths() {
        return includePaths;
    }

    public void setIncludePaths(List<String> includePaths) {
        this.includePaths = includePaths;
    }
}
