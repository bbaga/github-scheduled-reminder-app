package com.bbaga.githubscheduledreminderapp.domain.sources.github.filters;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHPullRequest;

public interface IssueFilterInterface {
    void configure(AbstractFilterConfig config);
    Boolean filter(GitHubIssue issue);
    Boolean filter(GitHubPullRequest issue);
}
