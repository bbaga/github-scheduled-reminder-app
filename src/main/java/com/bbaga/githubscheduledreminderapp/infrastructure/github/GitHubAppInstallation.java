package com.bbaga.githubscheduledreminderapp.infrastructure.github;

import org.kohsuke.github.GHAppInstallation;
import org.kohsuke.github.GHUser;

public class GitHubAppInstallation {
    private final GHAppInstallation installation;
    private GitHubAppInstallation(GHAppInstallation installation) {
        this.installation = installation;
    }

    public long getId() {
        return installation.getId();
    }

    public GHUser getAccount() {
        return installation.getAccount();
    }

    public GHAppInstallation unwrap() {
        return installation;
    }

    public static GitHubAppInstallation create(GHAppInstallation installation) {
        return new GitHubAppInstallation(installation);
    }
}
