package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.SourceConfig;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.filters.FilterChain;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import java.io.IOException;
import java.util.ArrayList;

public class RepositoryIssuesSource implements RepositoryAsSourceInterface<GitHubIssue> {

    private SourceConfig sourceConfig;

    @Override
    public void configure(SourceConfig sourceConfig) {
        this.sourceConfig = sourceConfig;
    }

    public ArrayList<GitHubIssue> get(GHRepository repository) throws IOException {
        return get(repository, GHIssueState.OPEN);
    }

    public ArrayList<GitHubIssue> get(GHRepository repository, GHIssueState state) throws IOException {
        ArrayList<GitHubIssue> issues = new ArrayList<>();
        repository.getIssues(state).forEach((GHIssue issue) -> {
            if (issue.isPullRequest()) {
                return;
            }

            GitHubIssue wrappedIssue = GitHubIssue.create(issue);

            if (FilterChain.filter(sourceConfig.getFilters(), wrappedIssue)) {
                return;
            }

            issues.add(wrappedIssue);
        });

        return issues;
    }
}
