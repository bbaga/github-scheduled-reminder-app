package com.bbaga.githubscheduledreminderapp.configuration;

import com.bbaga.githubscheduledreminderapp.configuration.configgraphnode.RepositoryRecord;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigGraphNode {
    private final Long installationId;
    private final Notification notification;
    private Instant lastSeenAt;
    private final ConcurrentHashMap<Integer, RepositoryRecord> repositories = new ConcurrentHashMap<>();

    public ConfigGraphNode(Long installationId, Notification notification, Instant seenAt) {
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
