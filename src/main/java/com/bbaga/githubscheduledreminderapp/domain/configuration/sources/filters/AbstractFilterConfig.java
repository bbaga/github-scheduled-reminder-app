package com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractFilterConfig {
    private String type;

    public AbstractFilterConfig(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
