package com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters;

public enum Filters {
    DRAFT_FILTER(Constants.DRAFT_FILTER),
    LABEL_FILTER(Constants.LABEL_FILTER),
    AUTHOR_FILTER(Constants.AUTHOR_FILTER),

    AUTHOR_EXCLUDE_FILTER(Constants.AUTHOR_EXCLUDE_FILTER),
    PATH_FILTER(Constants.PATH_FILTER);

    public final String label;

    Filters(String label) {
        this.label = label;
    }

    public static class Constants {
        public static final String DRAFT_FILTER = "draft-filter";
        public static final String LABEL_FILTER = "label-filter";
        public static final String AUTHOR_FILTER = "author-filter";
        public static final String AUTHOR_EXCLUDE_FILTER = "author-exclude-filter";
        public static final String PATH_FILTER = "path-filter";
    }
}
