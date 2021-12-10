package com.bbaga.githubscheduledreminderapp.infrastructure.github.repositories;

import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubAppInstallation;
import org.kohsuke.github.GHAppInstallation;

import java.util.HashMap;
import java.util.Set;

public class GitHubInstallationRepository {
    private final HashMap<Long, GitHubAppInstallation> installations = new HashMap<>();
    private final HashMap<String, Long> mapOrgToInstallation = new HashMap<>();

    public void put(GHAppInstallation installation) {
        GitHubAppInstallation container = GitHubAppInstallation.create(installation);
        synchronized (this.installations) {
            installations.put(container.getId(), container);
            mapOrgToInstallation.putIfAbsent(container.getAccount().getLogin(), container.getId());
        }
    }

    public void remove(Long installationId) {
        synchronized (this.installations) {
            GitHubAppInstallation container = installations.get(installationId);
            String key = container.getAccount().getLogin();
            mapOrgToInstallation.remove(key);
            installations.remove(installationId);
        }
    }

    public GitHubAppInstallation get(Long installationId) {
        synchronized (this.installations) {
            GitHubAppInstallation container = installations.get(installationId);

            if (container == null) {
                return null;
            }

            return container;
        }
    }

    public Long getIdByOrg(String org) {
        synchronized (this.installations) {
            return mapOrgToInstallation.get(org);
        }
    }

    public Set<Long> getIds() {
        synchronized (this.installations) {
            return installations.keySet();
        }
    }
}
