package com.bbaga.githubscheduledreminderapp.domain.configuration;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigGraphNode {
    private final Long installationId;
    private Notification notification;
    private Instant lastSeenAt;
    private final ConcurrentHashMap<Integer, RepositoryRecord> repositories = new ConcurrentHashMap<>();

    public ConfigGraphNode(long installationId, Notification notification, Instant seenAt) {
        this.installationId = installationId;
        this.notification = notification;
        setSeenAt(seenAt);
    }

    public Long getInstallationId() {
        return installationId;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public void putRepository(RepositoryRecord repositoryRecord) {
        repositories.put(repositoryRecord.hashCode(), repositoryRecord);
    }

    public ConcurrentHashMap<Integer, RepositoryRecord> getRepositories() {
        return repositories;
    }

    public void setSeenAt(Instant now) {
        this.lastSeenAt = now;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt;
    }
}
