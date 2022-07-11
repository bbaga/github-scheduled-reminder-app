package com.bbaga.githubscheduledreminderapp.infrastructure.github;

import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestFileDetail;

import java.io.IOException;
import java.util.stream.Collectors;

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

    public Iterable<String> listFileNames() throws IOException {
        return pullRequest.listFiles().toSet().stream().map(GHPullRequestFileDetail::getFilename).collect(Collectors.toSet());
    }

    public GHPullRequest unwrap() {
        return pullRequest;
    }

    public static GitHubPullRequest create(GHPullRequest pullRequest) {
        return new GitHubPullRequest(pullRequest);
    }
}
