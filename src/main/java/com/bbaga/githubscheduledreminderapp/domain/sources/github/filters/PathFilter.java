package com.bbaga.githubscheduledreminderapp.domain.sources.github.filters;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.PathFilterConfig;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;

import java.io.IOException;

public class PathFilter implements IssueFilterInterface {
    private PathFilterConfig config;

    @Override
    public void configure(AbstractFilterConfig config) {
        this.config = (PathFilterConfig) config;
    }

    @Override
    public Boolean filter(GitHubIssue issue) {
        // Issues don't contain files, so ignore this filter
        return false;
    }

    @Override
    public Boolean filter(GitHubPullRequest pullRequest) {
        var includePaths = config.getIncludePaths();
        if (includePaths == null || includePaths.isEmpty()) {
            // Nothing to filter by, so ignore this filter
            return false;
        }

        try {
            for (var file : pullRequest.getFilenames()) {
                for (var includePath : includePaths) {
                    if (file.startsWith(includePath)) {
                        return false;
                    }
                }
            }
        } catch (IOException ignore) {}

        return true;
    }
}
