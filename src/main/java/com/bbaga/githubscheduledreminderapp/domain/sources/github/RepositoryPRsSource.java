package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.Source;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;

import java.io.IOException;
import java.util.ArrayList;

public class RepositoryPRsSource implements RepositoryAsSourceInterface <GHIssue> {
    private Source source;

    @Override
    public void configure(Source source) {
        this.source = source;
    }

    public ArrayList<GHIssue> get(GHRepository repository) throws IOException {
        ArrayList<GHIssue> pullRequests = new ArrayList<>();

        repository.getPullRequests(GHIssueState.OPEN).forEach((GHPullRequest pr) -> {
            if (FilterChain.filter(source.getFilters(), pr)) {
                return;
            }

            pullRequests.add(pr);
        });

        return pullRequests;
    }
}
