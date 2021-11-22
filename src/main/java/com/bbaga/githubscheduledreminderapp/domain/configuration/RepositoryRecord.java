package com.bbaga.githubscheduledreminderapp.domain.configuration;

import java.time.Instant;

public class RepositoryRecord {
    private final String repository;
    private final Long installationId;
    private final Instant seenAt;

    public RepositoryRecord(String repository, Long installationId, Instant seenAt) {
        this.repository = repository;
        this.installationId = installationId;
        this.seenAt = seenAt;
    }

    public String getRepository() {
        return repository;
    }

    public Long getInstallationId() {
        return installationId;
    }

    public Instant getSeenAt() {
        return seenAt;
    }

    @Override
    public int hashCode() {
        return repository.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RepositoryRecord)) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        return obj.hashCode() == this.hashCode();
    }
}
