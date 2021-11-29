package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilter;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHPullRequest;

public class DraftFilter implements IssueFilterInterface {
    com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.DraftFilter config;

    @Override
    public void configure(AbstractFilter config) {
        this.config = (com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.DraftFilter) config;
    }

    @Override
    public Boolean filter(GHIssue issue) {
        return true;
    }

    @Override
    public Boolean filter(GHPullRequest pr) {
        try {
            return pr.isDraft();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
