package com.bbaga.githubscheduledreminderapp.domain.sources.github.filters;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.Filters;

public class FilterProvider {
    public static IssueFilterInterface get(AbstractFilterConfig config) {
        IssueFilterInterface filter;
        String type = config.getType();

        if (type.equals(Filters.DRAFT_FILTER.label)) {
            filter = new DraftFilter();
        } else if (type.equals(Filters.LABEL_FILTER.label)) {
            filter = new LabelFilter();
        } else if (type.equals(Filters.AUTHOR_FILTER.label)) {
            filter = new AuthorFilter();
        } else if (type.equals(Filters.PATH_FILTER.label)) {
            filter = new PathFilter();
        } else {
            throw new RuntimeException(String.format("Unknown filter type: \"%s\"", type));
        }

        filter.configure(config);

        return filter;
    }
}
