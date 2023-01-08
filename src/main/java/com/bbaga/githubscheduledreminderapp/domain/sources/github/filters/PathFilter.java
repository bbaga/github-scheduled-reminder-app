package com.bbaga.githubscheduledreminderapp.domain.sources.github.filters;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.PathFilterConfig;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;

import java.io.IOException;
import java.util.List;

public class PathFilter extends ExpireableFilter implements IssueFilterInterface {
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

        //No matter what if the filter is expired it should not filter results.
        if (isFilterExpired(pullRequest, config.getExpiryDays())) {
            return false;
        }

        List<String> includePaths = config.getIncludePaths();
        List<String> excludePaths = config.getExcludePaths();

        //Guard case - if both filters have nothing, filter nothing
        if ((includePaths == null || includePaths.isEmpty()) &&
            (excludePaths == null || excludePaths.isEmpty())) {
            return false;
        }

        boolean includePathSatisfied = false;
        boolean excludePathSatisfied = false;

        try {
            for (var file : pullRequest.getFilenames()) {
                if (includePaths != null && !includePaths.isEmpty()) {
                    for (var includePath : includePaths) {
                        if (file.startsWith(includePath)) {
                            includePathSatisfied = true;
                            break;
                        }
                    }
                }
                else if (excludePaths != null && !excludePaths.isEmpty()) {
                    for (var excludePath : excludePaths) {
                        if (file.startsWith(excludePath)) {
                            excludePathSatisfied = true;
                            break;
                        }
                    }
                }
            }
        } catch (IOException ignore) {}
        boolean result = false;

        // If we have includes use those.
        // Otherwise use excludes.
        if (includePaths != null && !includePaths.isEmpty()) {
            result = !includePathSatisfied;
        } else {
            result = excludePathSatisfied;
        }

        return result;
    }
}
