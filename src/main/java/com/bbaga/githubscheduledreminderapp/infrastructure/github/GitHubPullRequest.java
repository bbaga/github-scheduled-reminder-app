package com.bbaga.githubscheduledreminderapp.infrastructure.github;

import java.util.List;
import java.util.stream.Collectors;
import org.kohsuke.github.GHPullRequest;

import java.io.IOException;
import java.util.Iterator;

public class GitHubPullRequest extends GitHubIssue {
    private final GHPullRequest pullRequest;
    private GitHubPullRequest(GHPullRequest pullRequest) {
        super(pullRequest);
        this.pullRequest = pullRequest;
    }

    public String getMergeableState() throws IOException {
        return pullRequest.getMergeableState();
    }

    public Boolean getMergeable() throws IOException {
        return pullRequest.getMergeable();
    }

    public int getAdditions() throws IOException {
        return pullRequest.getAdditions();
    }

    public int getDeletions() throws IOException {
        return pullRequest.getDeletions();
    }

    public Boolean isDraft() throws IOException {
        return pullRequest.isDraft();
    }

    public Iterable<String> getFilenames() throws IOException {
        var iterator = pullRequest.listFiles().withPageSize(100).iterator();

        return () -> new Iterator<>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public String next() {
                return iterator.next().getFilename();
            }
        };
    }

    public List<GitHubUser> getRequestedReviewers() throws IOException {
        return pullRequest.getRequestedReviewers().stream().map(GitHubUser::create).collect(Collectors.toList());
    }

    public GHPullRequest unwrap() {
        return pullRequest;
    }

    public static GitHubPullRequest create(GHPullRequest pullRequest) {
        return new GitHubPullRequest(pullRequest);
    }
}
