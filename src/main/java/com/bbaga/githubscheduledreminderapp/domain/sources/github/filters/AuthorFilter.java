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
        List<String> excludeAuthors = config.getExcludeAuthors();

        //Guard case - if both filters have nothing, filter nothing
        if ((includeAuthors == null || includeAuthors.isEmpty()) &&
            (excludeAuthors == null || excludeAuthors.isEmpty())) {
            return false;
        }

        // Uses Include then exclude.
        // If a name exists in both it will be included - Therefore unfiltered - return false
        boolean result = false;

        try {
            String login = issue.getUser().getLogin();

            if (includeAuthors != null && !includeAuthors.isEmpty()) {
                result = !includeAuthors.contains(login);
            }
            else if (excludeAuthors != null && !excludeAuthors.isEmpty()) {
                result = excludeAuthors.contains(login);
            }
        } catch (IOException ignore) {}
        return result;
    }

    @Override
    public Boolean filter(GitHubPullRequest issue) {
        return this.filter((GitHubIssue) issue);
    }
}
