package com.bbaga.githubscheduledreminderapp.domain.configuration.sources;

public enum Sources {
    REPOSITORY_ISSUES(Constants.REPOSITORY_ISSUES),
    REPOSITORY_PRS(Constants.REPOSITORY_PRS),
    SEARCH_PRS_BY_REVIEWERS(Constants.SEARCH_PRS_BY_REVIEWERS),
    SEARCH_ISSUES(Constants.SEARCH_ISSUES);

    public final String label;

    Sources(String label) {
        this.label = label;
    }

    public static class Constants {
        public static final String REPOSITORY_ISSUES = "repository-issues";
        public static final String REPOSITORY_PRS = "repository-prs";
        public static final String SEARCH_PRS_BY_REVIEWERS = "search-prs-by-reviewers";
        public static final String SEARCH_ISSUES = "search-issues";
    }
}
