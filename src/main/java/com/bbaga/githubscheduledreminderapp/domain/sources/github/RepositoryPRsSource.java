package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.SourceConfig;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.filters.FilterChain;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;

import java.io.IOException;
import java.util.ArrayList;

public class RepositoryPRsSource implements RepositoryAsSourceInterface <GHIssue> {
    private SourceConfig sourceConfig;

    @Override
    public void configure(SourceConfig sourceConfig) {
        this.sourceConfig = sourceConfig;
    }

    public ArrayList<GHIssue> get(GHRepository repository) throws IOException {
        ArrayList<GHIssue> pullRequests = new ArrayList<>();

        repository.getPullRequests(GHIssueState.OPEN).forEach((GHPullRequest pr) -> {
            if (FilterChain.filter(sourceConfig.getFilters(), pr)) {
                return;
            }

            pullRequests.add(pr);
        });

        return pullRequests;
    }
}
