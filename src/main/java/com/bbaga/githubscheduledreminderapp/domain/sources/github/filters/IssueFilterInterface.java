package com.bbaga.githubscheduledreminderapp.domain.sources.github.filters;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHPullRequest;

public interface IssueFilterInterface {
    void configure(AbstractFilterConfig config);
    Boolean filter(GHIssue issue);
    Boolean filter(GHPullRequest issue);
}
