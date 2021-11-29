package com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters;

public enum Filters {
    DraftFilter(Constants.DRAFT_FILTER);

    public final String label;

    Filters(String label) {
        this.label = label;
    }

    public static class Constants {
        public static final String DRAFT_FILTER = "draft-filter";
    }
}
