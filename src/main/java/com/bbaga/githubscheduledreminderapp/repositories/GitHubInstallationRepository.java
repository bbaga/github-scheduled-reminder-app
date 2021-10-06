package com.bbaga.githubscheduledreminderapp.repositories;

import org.kohsuke.github.GHAppInstallation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class GitHubInstallationRepository {
    private final HashMap<Long, GHAppInstallation> installations = new HashMap<>();

    public void put(GHAppInstallation installation) {
        installations.put(installation.getId(), installation);
    }

    public void remove(Long installationId) {
        installations.remove(installationId);
    }

    public GHAppInstallation get(Long installationId) {
        return installations.get(installationId);
    }

    public Integer count() {
        return installations.size();
    }

    public Set<Long> getIds() {
        return installations.keySet();
    }

    public Collection<GHAppInstallation> getValues() {
        return installations.values();
    }
}
