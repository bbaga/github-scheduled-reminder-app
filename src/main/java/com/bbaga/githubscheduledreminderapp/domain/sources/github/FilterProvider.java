package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilter;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.Filters;

public class FilterProvider {
    public static IssueFilterInterface get(AbstractFilter config) {
        IssueFilterInterface filter;
        String type = config.getType();

        if (type.equals(Filters.DraftFilter.label)) {
            filter = new DraftFilter();
        } else {
            throw new RuntimeException(String.format("Unknown filter type: \"%s\"", type));
        }

        filter.configure(config);

        return filter;
    }
}
