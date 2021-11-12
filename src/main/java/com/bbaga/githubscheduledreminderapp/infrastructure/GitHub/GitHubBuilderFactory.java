package com.bbaga.githubscheduledreminderapp.infrastructure.GitHub;

import org.kohsuke.github.GitHubBuilder;

public class GitHubBuilderFactory {
    private final String endpoint;

    public GitHubBuilderFactory(String gitHubApiEndpoint) {
        this.endpoint = gitHubApiEndpoint;
    }

    public GitHubBuilder create() {
        if (endpoint.isEmpty()) {
            return new GitHubBuilder();
        }

        return new GitHubBuilder().withEndpoint(endpoint);
    }
}
