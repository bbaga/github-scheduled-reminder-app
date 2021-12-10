package com.bbaga.githubscheduledreminderapp.infrastructure.github;

import org.kohsuke.github.GHUser;

public class GitHubUser {
    private final GHUser user;

    private GitHubUser(GHUser user) {
        this.user = user;
    }

    public String getLogin() {
        return user.getLogin();
    }

    public GHUser unwrap() {
        return user;
    }

    public static GitHubUser create(GHUser user) {
        return new GitHubUser(user);
    }
}
