package com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DraftFilterConfig extends AbstractFilterConfig {

    @JsonProperty("include-drafts")
    private Boolean includeDrafts = false;

    @JsonProperty("expiry-days")
    private int expiryDays = Integer.MAX_VALUE;

    public DraftFilterConfig() {
        super(Filters.DRAFT_FILTER.label);
    }

    public Boolean getIncludeDrafts() {
        return includeDrafts;
    }

    public void setIncludeDrafts(Boolean includeDrafts) {
        this.includeDrafts = includeDrafts;
    }

    public int getExpiryDays() {
        return expiryDays;
    }

    public void setExpiryDays(int expiryDays) {
        this.expiryDays = expiryDays;
    }
}
