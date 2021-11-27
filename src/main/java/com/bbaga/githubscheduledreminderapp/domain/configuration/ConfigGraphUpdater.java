package com.bbaga.githubscheduledreminderapp.domain.configuration;

import com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling.NotificationJobScheduler;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigGraphUpdater {
    private final ConcurrentHashMap<String, ConfigGraphNode> configGraph;
    private final NotificationJobScheduler notificationJobScheduler;
    private final Logger logger = LoggerFactory.getLogger(ConfigGraphUpdater.class);

    public ConfigGraphUpdater(
            ConcurrentHashMap<String, ConfigGraphNode> configGraph,
            NotificationJobScheduler notificationJobScheduler
    ) {
        this.configGraph = configGraph;
        this.notificationJobScheduler = notificationJobScheduler;
    }

    public void updateEntry(
        NotificationInterface notification,
        long installationId,
        String repositoryFullName,
        Instant timestamp
    ) throws SchedulerException {
        if (notification instanceof Notification) {
            Notification main = (Notification) notification;
            if (main.getSchedule().isPresent()) {
                updateSchedule(main, installationId, repositoryFullName, timestamp);
                return;
            }
        }

        updateRepoEntry(notification, installationId, repositoryFullName, timestamp);
    }

    private void updateRepoEntry(NotificationInterface notification, long installationId, String repositoryFullName, Instant timestamp) {
        logger.info("Updating repository entry: installation id {}, in {}", installationId, repositoryFullName);
        Extending extending = null;

        if (notification instanceof Extending) {
            extending = (Extending) notification;
        }

        if (extending == null || extending.getExtending().getRepository().isBlank() || extending.getExtending().getName().isBlank()) {
            return;
        }

        String notificationKey = getNotificationKey(extending);
        if (configGraph.containsKey(notificationKey)) {
            configGraph.get(notificationKey).putRepository(new RepositoryRecord(repositoryFullName, installationId, timestamp));
        }
    }

    private void updateSchedule(Notification notification, long installationId, String repositoryFullName, Instant timestamp) throws SchedulerException {
        logger.info("Updating schedule: installation id {}, {}/{}", installationId, repositoryFullName, notification.getName());
        notification.setName(getNotificationKey(repositoryFullName, notification.getName()));
        String notificationKey = notification.getName();

        if (!configGraph.containsKey(notificationKey)) {
            configGraph.put(notificationKey, new ConfigGraphNode(installationId, notification, timestamp));
            notificationJobScheduler.createSchedule(notification);
        } else {
            ConfigGraphNode node = configGraph.get(notificationKey);
            node.setNotification(notification);
            node.setSeenAt(timestamp);
            notificationJobScheduler.updateSchedule(notification);
        }
    }

    public void clearOutdated(Long installationId, Instant timestamp) {
        configGraph.entrySet().removeIf(entry -> {
            ConfigGraphNode node = entry.getValue();
            if (node.getInstallationId().equals(installationId) && node.getLastSeenAt() != timestamp) {
                return true;
            }

            node.getRepositories().entrySet().removeIf(repoEntry -> {
                RepositoryRecord repositoryRecord = repoEntry.getValue();
                return repositoryRecord.getInstallationId().equals(installationId)
                        && repositoryRecord.getSeenAt() != timestamp;
            });

            return false;
        });
    }

    public void clearOutdated(Long installationId, String repositoryFullName, Instant timestamp) {
        configGraph.entrySet().removeIf(entry -> {
            ConfigGraphNode node = entry.getValue();
            if (node.getInstallationId().equals(installationId)
                && node.getNotification().getName().startsWith(repositoryFullName)
                && node.getLastSeenAt() != timestamp
            ) {
                return true;
            }

            node.getRepositories().entrySet().removeIf(repoEntry -> {
                RepositoryRecord repositoryRecord = repoEntry.getValue();
                return repositoryRecord.getInstallationId().equals(installationId)
                        && repositoryRecord.getRepository().equals(repositoryFullName)
                        && repositoryRecord.getSeenAt() != timestamp;
            });

            return false;
        });
    }

    public void removeRepository(Long installationId, String repositoryFullName) {
        configGraph.forEach((key, node) -> node.getRepositories().entrySet().removeIf(repoEntry -> {
            RepositoryRecord repositoryRecord = repoEntry.getValue();
            return repositoryRecord.getInstallationId().equals(installationId)
                    && repositoryRecord.getRepository().equals(repositoryFullName);
        }));
    }

    private String getNotificationKey(Extending extending) {
        return getNotificationKey(extending.getExtending().getRepository(), extending.getExtending().getName());
    }

    private String getNotificationKey(String repository, String name) {
        return String.format("%s-%s", repository, name);
    }
}
