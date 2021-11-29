package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.Source;
import org.kohsuke.github.GHIssue;

import static com.bbaga.githubscheduledreminderapp.domain.configuration.sources.Sources.Constants.*;

public class SourceProvider {
    public static RepositoryAsSourceInterface<GHIssue> getRepositoryAsSourceProvider(Source config) {
        RepositoryAsSourceInterface<GHIssue> source;
        String type = config.getType();

        if (type.equals(REPOSITORY_PRS)) {
            source = new RepositoryPRsSource();
        } else if (type.equals(REPOSITORY_ISSUES)) {
            source = new RepositoryIssuesSource();
        } else {
            throw new RuntimeException(String.format("Unknown source type: \"%s\"", type));
        }

        source.configure(config);

        return source;
    }

    public static SearchAsSourceInterface<GHIssue> getSearchAsSourceProvider(Source config) {
        SearchAsSourceInterface<GHIssue> source;
        String type = config.getType();

        if (type.equals(SEARCH_PRS_BY_REVIEWERS)) {
            source = new SearchByReviewersPRsSource();
        } else if (type.equals(SEARCH_ISSUES)) {
            source = new SearchIssuesSource();
        } else {
            throw new RuntimeException(String.format("Unknown source type: \"%s\"", type));
        }

        source.configure(config);

        return source;
    }
}
