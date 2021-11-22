package com.bbaga.githubscheduledreminderapp.infrastructure.github;

import org.kohsuke.github.GHAppInstallation;

public class AppInstallationContainer {
    private final GHAppInstallation installation;
    private AppInstallationContainer(GHAppInstallation installation) {
        this.installation = installation;
    }

    public long getId() {
        return installation.getId();
    }

    public GHAppInstallation unwrap() {
        return installation;
    }

    public static AppInstallationContainer create(GHAppInstallation installation) {
        return new AppInstallationContainer(installation);
    }
}
