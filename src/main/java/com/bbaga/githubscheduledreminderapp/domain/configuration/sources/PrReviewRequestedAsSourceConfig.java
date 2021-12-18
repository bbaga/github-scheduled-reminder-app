package com.bbaga.githubscheduledreminderapp.domain.configuration.sources;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;

import java.util.ArrayList;
import java.util.List;

public class PrReviewRequestedAsSourceConfig extends EventAsSourceConfig {
    private List<String> actions = new ArrayList<>();
    private List<String> requestedReviewers = new ArrayList<>();
    private List<String> requestedTeams = new ArrayList<>();

    public PrReviewRequestedAsSourceConfig(String type, ArrayList<AbstractFilterConfig> filters) {
        super(type, "pull_request", filters);
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    @Override
    public Boolean hasEventAsSource() {
        return true;
    }
}
