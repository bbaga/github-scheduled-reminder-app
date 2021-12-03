package com.bbaga.githubscheduledreminderapp.domain.configuration.sources;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchPRsByReviewersSourceConfig extends SearchAsSourceConfig {
    private ArrayList<String> teams = new ArrayList<>();
    private ArrayList<String> users = new ArrayList<>();

    public SearchPRsByReviewersSourceConfig() {
        super(Sources.SEARCH_PRS_BY_REVIEWERS.label, new ArrayList<>());
        users = new ArrayList<>();
        teams = new ArrayList<>();
    }

    public SearchPRsByReviewersSourceConfig(String type) {
        super(type, new ArrayList<>());
    }

    public SearchPRsByReviewersSourceConfig(String type, ArrayList<AbstractFilterConfig> filters) {
        super(type, filters);
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    public ArrayList<String> getTeams() {
        return teams;
    }

    public void setTeams(ArrayList<String> teams) {
        this.teams = teams;
    }
}
