package com.bbaga.githubscheduledreminderapp.infrastructure.github;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;

public class GitHubIssue {
    private final GHIssue issue;
    public GitHubIssue(GHIssue issue) {
        this.issue = issue;
    }

    public long getId() {
        return issue.getId();
    }

    public int getNumber() {
        return issue.getNumber();
    }

    public GitHubUser getUser() throws IOException {
        return GitHubUser.create(issue.getUser());
    }

    public URL getHtmlUrl() {
        return issue.getHtmlUrl();
    }

    public String getTitle() {
        return issue.getTitle();
    }

    public Collection<GHLabel> getLabels() {
        return issue.getLabels();
    }

    public GHRepository getRepository() {
        return issue.getRepository();
    }

    public Date getCreatedAt() throws IOException {
        return issue.getCreatedAt();
    }

    public Date getUpdatedAt() throws IOException {
        return issue.getUpdatedAt();
    }

    public String getNodeId() {
        return issue.getNodeId();
    }

    public GHIssue unwrap() {
        return issue;
    }

    public Boolean isPullRequest() {
        return issue.isPullRequest();
    }

    public static GitHubIssue create(GHIssue issue) {
        return new GitHubIssue(issue);
    }
}
