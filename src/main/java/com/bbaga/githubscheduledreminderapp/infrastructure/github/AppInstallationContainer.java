package com.bbaga.githubscheduledreminderapp.infrastructure.github;

import org.kohsuke.github.GHAppInstallation;
import org.kohsuke.github.GHUser;

public class AppInstallationContainer {
    private final GHAppInstallation installation;
    private AppInstallationContainer(GHAppInstallation installation) {
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

    public static AppInstallationContainer create(GHAppInstallation installation) {
        return new AppInstallationContainer(installation);
    }
}
