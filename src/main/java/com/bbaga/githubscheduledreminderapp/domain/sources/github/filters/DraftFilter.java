package com.bbaga.githubscheduledreminderapp.domain.sources.github.filters;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.DraftFilterConfig;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;

public class DraftFilter extends ExpireableFilter implements IssueFilterInterface {
    private DraftFilterConfig config;

    @Override
    public void configure(AbstractFilterConfig config) {
        this.config = (DraftFilterConfig) config;
    }

    @Override
    public Boolean filter(GitHubIssue issue) {
        return false;
    }

    @Override
    public Boolean filter(GitHubPullRequest pr) {
        //No matter what if the filter is expired it should not filter results.
        if (isFilterExpired(pr, config.getExpiryDays())) {
            return false;
        }

        try {
            return !config.getIncludeDrafts() && pr.isDraft();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
