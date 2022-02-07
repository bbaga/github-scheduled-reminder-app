package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.SourceConfig;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.filters.FilterChain;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;
import org.kohsuke.github.GHDirection;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestQueryBuilder;
import org.kohsuke.github.GHRepository;

import java.io.IOException;
import java.util.ArrayList;

public class RepositoryPRsSource implements RepositoryAsSourceInterface <GitHubIssue> {
    private SourceConfig sourceConfig;

    @Override
    public void configure(SourceConfig sourceConfig) {
        this.sourceConfig = sourceConfig;
    }

    public ArrayList<GitHubIssue> get(GHRepository repository) throws IOException {
        ArrayList<GitHubIssue> pullRequests = new ArrayList<>();

        repository.queryPullRequests()
            .state(GHIssueState.OPEN)
            .sort(GHPullRequestQueryBuilder.Sort.CREATED)
            .direction(GHDirection.DESC)
            .list()
            .withPageSize(100)
            .forEach((GHPullRequest pr) -> {
                GitHubPullRequest wrappedPr = GitHubPullRequest.create(pr);

                if (FilterChain.filter(sourceConfig.getFilters(), wrappedPr)) {
                    return;
                }

                pullRequests.add(wrappedPr);
          });

        return pullRequests;
    }
}
