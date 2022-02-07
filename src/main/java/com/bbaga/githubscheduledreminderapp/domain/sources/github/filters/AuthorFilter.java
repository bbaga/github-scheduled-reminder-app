package com.bbaga.githubscheduledreminderapp.domain.sources.github.filters;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AuthorFilterConfig;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;
import java.io.IOException;
import java.util.List;

public class AuthorFilter implements IssueFilterInterface {
    private AuthorFilterConfig config;

    @Override
    public void configure(AbstractFilterConfig config) {
        this.config = (AuthorFilterConfig) config;
    }

    @Override
    public Boolean filter(GitHubIssue issue) {
        List<String> includeAuthors = config.getIncludeAuthors();

        try {
            return includeAuthors == null || !includeAuthors.contains(issue.getUser().getLogin());
        } catch (IOException ignore) {}

        return false;
    }

    @Override
    public Boolean filter(GitHubPullRequest issue) {
        return this.filter((GitHubIssue) issue);
    }
}
