package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilter;
import org.kohsuke.github.GHIssue;

import java.util.List;

public class FilterChain {
    public static Boolean filter(List<AbstractFilter> filterList, GHIssue issue) {
        if (filterList != null) {
            for (AbstractFilter filter : filterList) {
                if (FilterProvider.get(filter).filter(issue)) {
                    return true;
                }
            }
        }

        return false;
    }
}
