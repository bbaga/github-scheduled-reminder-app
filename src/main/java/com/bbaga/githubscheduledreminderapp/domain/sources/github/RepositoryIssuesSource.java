package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.Source;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import java.io.IOException;
import java.util.ArrayList;

public class RepositoryIssuesSource implements RepositoryAsSourceInterface<GHIssue> {

    private Source source;

    @Override
    public void configure(Source source) {
        this.source = source;
    }

    public ArrayList<GHIssue> get(GHRepository repository) throws IOException {
        return get(repository, GHIssueState.OPEN);
    }

    public static ArrayList<GHIssue> get(GHRepository repository, GHIssueState state) throws IOException {
        ArrayList<GHIssue> issues = new ArrayList<>();
        repository.getIssues(state).forEach((GHIssue issue) -> {
            if (!issue.isPullRequest()) {
                issues.add(issue);
            }
        });

        return issues;
    }
}
