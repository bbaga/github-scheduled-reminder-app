package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class RepositoryIssuesSource {
    public static Set<GHIssue> get(GHRepository repository) throws IOException {
        Set<GHIssue> issues = new HashSet<>();
        repository.getIssues(GHIssueState.OPEN).forEach((GHIssue issue) -> {
            if (!issue.isPullRequest()) {
                issues.add(issue);
            }
        });

        return issues;
    }
}
