package com.bbaga.githubscheduledreminderapp.domain.configuration.configGraphUpdater;

import java.time.Instant;

class EntryContext {
    private final String repositoryFullName;
    private final Instant timestamp;
    private final long installationId;

    public EntryContext(long installationId, String repositoryFullName, Instant timestamp) {
        this.repositoryFullName = repositoryFullName;
        this.timestamp = timestamp;
        this.installationId = installationId;
    }

    public String getRepositoryFullName() {
        return repositoryFullName;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public long getInstallationId() {
        return installationId;
    }
}
