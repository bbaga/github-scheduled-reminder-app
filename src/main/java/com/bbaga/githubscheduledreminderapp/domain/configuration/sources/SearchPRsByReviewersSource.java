package com.bbaga.githubscheduledreminderapp.domain.configuration.sources;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchPRsByReviewersSource extends SearchAsSource {
    private ArrayList<String> teams = new ArrayList<>();
    private ArrayList<String> users = new ArrayList<>();

    public SearchPRsByReviewersSource() {
        super(Sources.SEARCH_PRS_BY_REVIEWERS.label, new ArrayList<>());
        users = new ArrayList<>();
        teams = new ArrayList<>();
    }

    public SearchPRsByReviewersSource(String type) {
        super(type, new ArrayList<>());
    }

    public SearchPRsByReviewersSource(String type, ArrayList<AbstractFilter> filters) {
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
