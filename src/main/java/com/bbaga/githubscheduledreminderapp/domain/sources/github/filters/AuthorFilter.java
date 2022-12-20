package com.bbaga.githubscheduledreminderapp.domain.sources.github.filters;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AuthorFilterConfig;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;
import java.io.IOException;
import java.util.List;

public class AuthorFilter extends ExpireableFilter implements IssueFilterInterface{
    private AuthorFilterConfig config;

    @Override
    public void configure(AbstractFilterConfig config) {
        this.config = (AuthorFilterConfig) config;
    }

    @Override
    public Boolean filter(GitHubIssue issue) {

        //No matter what if the filter is expired it should not filter results.
        if (isFilterExpired(issue, config.getExpiryDays())) {
            return false;
        }

        List<String> includeAuthors = config.getIncludeAuthors();
        List<String> excludeAuthors = config.getExcludeAuthors();

        //Guard case - if both filters have nothing, filter nothing
        if ((includeAuthors == null || includeAuthors.isEmpty()) &&
            (excludeAuthors == null || excludeAuthors.isEmpty())) {
            return false;
        }

        // Uses Include over exclude.
        // If a name exists in both it will be included - Therefore unfiltered - return false
        try {
            String login = issue.getUser().getLogin();

            if (includeAuthors != null && !includeAuthors.isEmpty()) {
                return !includeAuthors.contains(login);
            }
            else if (excludeAuthors != null && !excludeAuthors.isEmpty()) {
                return excludeAuthors.contains(login);
            }
        } catch (IOException ignore) {}
        return false;
    }

    @Override
    public Boolean filter(GitHubPullRequest pullRequest) {
        return this.filter((GitHubIssue) pullRequest);
    }
}
