package com.bbaga.githubscheduledreminderapp.domain.sources.github.filters;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHPullRequest;

import java.util.List;

public class FilterChain {
    public static Boolean filter(List<AbstractFilterConfig> filterList, GHIssue issue) {
        Boolean result;

        if (filterList == null) {
            return false;
        }

        for (AbstractFilterConfig filterConfig : filterList) {
            if (issue instanceof GHPullRequest) {
                result = getFilter(filterConfig).filter((GHPullRequest) issue);
            } else {
                result = getFilter(filterConfig).filter(issue);
            }

            if (result) {
                return true;
            }
        }

        return false;
    }

    private static IssueFilterInterface getFilter(AbstractFilterConfig config) {
        return FilterProvider.get(config);
    }
}
