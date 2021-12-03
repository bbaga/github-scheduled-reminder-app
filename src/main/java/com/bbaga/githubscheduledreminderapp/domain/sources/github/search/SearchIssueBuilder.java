package com.bbaga.githubscheduledreminderapp.domain.sources.github.search;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueSearchBuilder;

public class SearchIssueBuilder {
    private final GHIssueSearchBuilder builder;

    public static SearchIssueBuilder from(GHIssueSearchBuilder issueSearchBuilder) {
        return new SearchIssueBuilder(issueSearchBuilder);
    }

    private SearchIssueBuilder(GHIssueSearchBuilder issueSearchBuilder) {
        this.builder = issueSearchBuilder;
    }

    public Iterable<GHIssue> query(String query) {
        return builder.q(query).list();
    }
}
