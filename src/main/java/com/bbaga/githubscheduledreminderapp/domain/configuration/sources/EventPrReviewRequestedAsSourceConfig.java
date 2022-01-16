package com.bbaga.githubscheduledreminderapp.domain.configuration.sources;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;

import java.util.ArrayList;
import java.util.List;

public class EventPrReviewRequestedAsSourceConfig extends EventAsSourceConfig {
    private List<String> actions = new ArrayList<>();
    private List<String> reviewers = new ArrayList<>();
    private List<String> teams = new ArrayList<>();

    public EventPrReviewRequestedAsSourceConfig() {
        super(Sources.EVENT_PR_REVIEW_REQUESTED.label, "pull_request", new ArrayList<>());
    }

    public EventPrReviewRequestedAsSourceConfig(String type, ArrayList<AbstractFilterConfig> filters) {
        super(type, "pull_request", filters);
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public List<String> getReviewers() {
        return reviewers;
    }

    public void setReviewers(List<String> reviewers) {
        this.reviewers = reviewers;
    }

    public List<String> getTeams() {
        return teams;
    }

    public void setTeams(List<String> teams) {
        this.teams = teams;
    }

    @Override
    public Boolean hasEventAsSource() {
        return true;
    }
}
