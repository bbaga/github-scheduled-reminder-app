package com.bbaga.githubscheduledreminderapp.repositories;

import org.kohsuke.github.GHAppInstallation;

import java.util.HashMap;
import java.util.Set;

public class GitHubInstallationRepository {
    private final HashMap<Long, GHAppInstallation> installations = new HashMap<>();

    public void put(GHAppInstallation installation) {
        synchronized (this.installations) {
            installations.put(installation.getId(), installation);
        }
    }

    public void remove(Long installationId) {
        synchronized (this.installations) {
            installations.remove(installationId);
        }
    }

    public GHAppInstallation get(Long installationId) {
        synchronized (this.installations) {
            return installations.get(installationId);
        }
    }

    public Set<Long> getIds() {
        synchronized (this.installations) {
            return installations.keySet();
        }
    }
}
