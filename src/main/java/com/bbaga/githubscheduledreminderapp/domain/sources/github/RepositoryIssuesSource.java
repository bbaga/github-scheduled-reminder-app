package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.SourceConfig;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.filters.FilterChain;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import java.io.IOException;
import java.util.ArrayList;

public class RepositoryIssuesSource implements RepositoryAsSourceInterface<GHIssue> {

    private SourceConfig sourceConfig;

    @Override
    public void configure(SourceConfig sourceConfig) {
        this.sourceConfig = sourceConfig;
    }

    public ArrayList<GHIssue> get(GHRepository repository) throws IOException {
        return get(repository, GHIssueState.OPEN);
    }

    public ArrayList<GHIssue> get(GHRepository repository, GHIssueState state) throws IOException {
        ArrayList<GHIssue> issues = new ArrayList<>();
        repository.getIssues(state).forEach((GHIssue issue) -> {
            if (issue.isPullRequest()) {
                return;
            }

            if (FilterChain.filter(sourceConfig.getFilters(), issue)) {
                return;
            }

            issues.add(issue);
        });

        return issues;
    }
}
