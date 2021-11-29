package com.bbaga.githubscheduledreminderapp.domain.configuration;

import java.time.Instant;

public class RepositoryRecord {
    private final String repository;
    private final Long installationId;
    private final Instant seenAt;
    private final NotificationConfigurationInterface config;

    public RepositoryRecord(String repository, Long installationId, Instant seenAt, NotificationConfigurationInterface config) {
        this.repository = repository;
        this.installationId = installationId;
        this.seenAt = seenAt;
        this.config = config;
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

    public NotificationConfigurationInterface getConfig() {
        return config;
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
