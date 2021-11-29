package com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DraftFilter extends AbstractFilter {

    @JsonProperty("include-drafts")
    private Boolean includeDrafts = false;

    public Boolean getIncludeDrafts() {
        return includeDrafts;
    }

    public void setIncludeDrafts(Boolean includeDrafts) {
        this.includeDrafts = includeDrafts;
    }
}
