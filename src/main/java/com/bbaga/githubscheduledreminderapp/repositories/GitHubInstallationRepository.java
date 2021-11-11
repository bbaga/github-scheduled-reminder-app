package com.bbaga.githubscheduledreminderapp.repositories;

import com.bbaga.githubscheduledreminderapp.infrastructure.GitHub.AppInstallationContainer;
import org.kohsuke.github.GHAppInstallation;

import java.util.HashMap;
import java.util.Set;

public class GitHubInstallationRepository {
    private final HashMap<Long, AppInstallationContainer> installations = new HashMap<>();

    public void put(GHAppInstallation installation) {
        AppInstallationContainer container = AppInstallationContainer.create(installation);
        synchronized (this.installations) {
            installations.put(container.getId(), container);
        }
    }

    public void remove(Long installationId) {
        synchronized (this.installations) {
            installations.remove(installationId);
        }
    }

    public GHAppInstallation get(Long installationId) {
        synchronized (this.installations) {
            AppInstallationContainer container = installations.get(installationId);

            if (container == null) {
                return null;
            }

            return container.unwrap();
        }
    }

    public Set<Long> getIds() {
        synchronized (this.installations) {
            return installations.keySet();
        }
    }
}
