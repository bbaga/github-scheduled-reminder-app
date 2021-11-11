package com.bbaga.githubscheduledreminderapp.configuration;

import com.bbaga.githubscheduledreminderapp.configuration.configgraphnode.RepositoryRecord;
import org.quartz.*;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigGraphUpdater {
    private final ConcurrentHashMap<String, ConfigGraphNode> configGraph;
    private final Scheduler scheduler;

    public ConfigGraphUpdater(
            ConcurrentHashMap<String, ConfigGraphNode> configGraph,
            Scheduler scheduler
    ) {
        this.configGraph = configGraph;
        this.scheduler = scheduler;
    }

    public void updateEntry(
        Notification notification,
        long installationId,
        String repositoryFullName,
        Instant timestamp
    ) throws SchedulerException {
        if (notification.getSchedule() != null) {
            updateSchedule(notification, installationId, repositoryFullName, timestamp);
            return;
        }

        updateRepoEntry(notification, installationId, repositoryFullName, timestamp);
    }

    private void updateRepoEntry(Notification notification, long installationId, String repositoryFullName, Instant timestamp) {
        Extending extending = notification.getExtending();

        if (extending.getRepository() == null || extending.getName() == null) {
            return;
        }

        String notificationKey = getNotificationKey(extending);
        if (configGraph.containsKey(notificationKey)) {
            configGraph.get(notificationKey).putRepository(new RepositoryRecord(repositoryFullName, installationId, timestamp));
        }
    }

    private void updateSchedule(Notification notification, long installationId, String repositoryFullName, Instant timestamp) throws SchedulerException {
        notification.setName(getNotificationKey(repositoryFullName, notification.getName()));
        String notificationKey = notification.getName();

        if (!configGraph.containsKey(notificationKey)) {
            configGraph.put(notificationKey, new ConfigGraphNode(installationId, notification, timestamp));
        } else {
            ConfigGraphNode node = configGraph.get(notificationKey);
            node.setNotification(notification);
            node.setSeenAt(timestamp);

            Trigger newTrigger = TriggerBuilder.newTrigger()
                    .withSchedule(CronScheduleBuilder.cronSchedule(node.getNotification().getSchedule()))
                    .withIdentity(notificationKey).build();
            scheduler.rescheduleJob(new TriggerKey(notificationKey), newTrigger);
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

    private String getNotificationKey(Extending extending) {
        return getNotificationKey(extending.getRepository(), extending.getName());
    }

    private String getNotificationKey(String repository, String name) {
        return String.format("%s-%s", repository, name);
    }
}
