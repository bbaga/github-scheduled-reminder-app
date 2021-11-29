package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilter;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHPullRequest;

public interface IssueFilterInterface {
    public void configure(AbstractFilter config);
    public Boolean filter(GHIssue issue);
    public Boolean filter(GHPullRequest issue);
}
