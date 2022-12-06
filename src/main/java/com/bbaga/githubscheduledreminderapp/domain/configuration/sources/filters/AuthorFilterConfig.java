package com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorFilterConfig extends AbstractFilterConfig {

    @JsonProperty("include")
    private List<String> includeAuthors;

    @JsonProperty("exclude")
    private List<String> excludeAuthors;

    public AuthorFilterConfig() {
        super(Filters.AUTHOR_FILTER.label);
        includeAuthors = new ArrayList<>();
        excludeAuthors = new ArrayList<>();
    }

    public List<String> getIncludeAuthors() {
        return includeAuthors;
    }

    public void setIncludeAuthors(List<String> includeAuthors) {
        this.includeAuthors = includeAuthors;
    }


    public List<String> getExcludeAuthors() {
        return excludeAuthors;
    }

    public void setExcludeAuthors(List<String> excludeAuthors) {
        this.excludeAuthors = excludeAuthors;
    }
}
