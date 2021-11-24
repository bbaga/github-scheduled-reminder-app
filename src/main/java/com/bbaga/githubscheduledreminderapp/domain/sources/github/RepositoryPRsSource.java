package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class RepositoryPRsSource {
    public static Set<GHIssue> get(GHRepository repository) throws IOException {
        Set<GHIssue> pullRequests = new HashSet<>();
        repository.getPullRequests(GHIssueState.OPEN).forEach((GHPullRequest pr) -> {
            try {
                if (!pr.isDraft()) {
                    pullRequests.add(pr);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return pullRequests;
    }
}
