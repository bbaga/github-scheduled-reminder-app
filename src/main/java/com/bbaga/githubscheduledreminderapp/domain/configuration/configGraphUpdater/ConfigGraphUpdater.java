package com.bbaga.githubscheduledreminderapp.domain.configuration.configGraphUpdater;

import com.bbaga.githubscheduledreminderapp.domain.configuration.*;
import com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling.NotificationJobScheduler;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigGraphUpdater {
    private final ConcurrentHashMap<String, ConfigGraphNode> configGraph;
    private final ConcurrentHashMap<String, ConcurrentHashMap<Integer, RepositoryRecord>> buffer;
    private final NotificationJobScheduler notificationJobScheduler;
    private final Logger logger = LoggerFactory.getLogger(ConfigGraphUpdater.class);
    private final ConfigVisitorFactoryFactory configVisitorFactoryFactory;

    public ConfigGraphUpdater(
        ConfigVisitorFactoryFactory configVisitorFactoryFactory,
        ConcurrentHashMap<String, ConfigGraphNode> configGraph,
        NotificationJobScheduler notificationJobScheduler
    ) {
        this.configVisitorFactoryFactory = configVisitorFactoryFactory;
        this.configGraph = configGraph;
        this.notificationJobScheduler = notificationJobScheduler;
        this.buffer = new ConcurrentHashMap<>();
    }

    public void updateEntry(
        NotificationInterface notification,
        long installationId,
        String repositoryFullName,
        Instant timestamp
    ) throws SchedulerException {
        EntryContext context = new EntryContext(installationId, repositoryFullName, timestamp);

        NotificationVisitor notificationVisitor = new NotificationVisitor(
            this,
            configVisitorFactoryFactory.create(this),
            context
        );

        notification.accept(notificationVisitor);
    }

    void updateRepoEntry(Extending notification, long installationId, String repositoryFullName, Instant timestamp) {
        logger.info("Updating repository entry: installation id {}, in {}", installationId, repositoryFullName);

        if (notification == null || notification.getExtending().getRepository().isBlank() || notification.getExtending().getName().isBlank()) {
            return;
        }

        String notificationKey = getNotificationKey(notification);
        RepositoryRecord record = new RepositoryRecord(repositoryFullName, installationId, timestamp, notification.getConfig());

        if (configGraph.containsKey(notificationKey)) {
            configGraph.get(notificationKey).putRepository(record);
        } else {
            addToBuffer(notificationKey, record);
        }
    }

    void upsertSchedule(Notification notification, long installationId, String repositoryFullName, Instant timestamp) throws SchedulerException {
        logger.info("Updating schedule: installation id {}, {}/{}", installationId, repositoryFullName, notification.getName());
        notification.setRepository(repositoryFullName);
        String notificationKey = notification.getFullName();

        if (!configGraph.containsKey(notificationKey)) {
            configGraph.put(notificationKey, new ConfigGraphNode(installationId, notification, timestamp));
        } else {
            ConfigGraphNode node = configGraph.get(notificationKey);
            node.setNotification(notification);
            node.setSeenAt(timestamp);
        }

        if (notification.getConfig() instanceof ScheduledNotificationConfigurationInterface) {
            notificationJobScheduler.upsertSchedule(
                notification.getFullName(),
                (ScheduledNotificationConfigurationInterface) notification.getConfig()
            );
        }

        addBuffered(notification);
    }

    public void  clearOutdated(Long installationId, Instant timestamp) {
        configGraph.entrySet().removeIf(entry -> {
            ConfigGraphNode node = entry.getValue();
            if (node.getInstallationId().equals(installationId) && node.getLastSeenAt() != timestamp) {
                return true;
            }

            node.getRepositories().entrySet().removeIf(repoEntry -> {
                RepositoryRecord repositoryRecord = repoEntry.getValue();

                NotificationConfigurationInterface notificationConfig = node.getNotification().getConfig();

                if (repositoryRecord.getSeenAt() != timestamp) {
                    boolean isLocalConfig = notificationConfig instanceof RepositoryAwareNotificationConfiguration
                        && ((RepositoryAwareNotificationConfiguration) notificationConfig).getRepositories() != null
                        && ((RepositoryAwareNotificationConfiguration) notificationConfig).getRepositories().containsKey(repositoryRecord.getRepository());

                    return repositoryRecord.getInstallationId().equals(installationId) && !isLocalConfig;
                }

                return false;
            });

            return false;
        });
    }

    public void clearOutdated(Long installationId, String repositoryFullName, Instant timestamp) {
        configGraph.entrySet().removeIf(entry -> {
            ConfigGraphNode node = entry.getValue();
            if (node.getInstallationId().equals(installationId)
                && node.getNotification().getFullName().startsWith(repositoryFullName)
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

    public ConcurrentHashMap<String, ConcurrentHashMap<Integer, RepositoryRecord>> getBuffer() {
        return buffer;
    }

    private String getNotificationKey(Extending extending) {
        return getNotificationKey(extending.getExtending().getRepository(), extending.getExtending().getName());
    }

    private String getNotificationKey(String repository, String name) {
        return String.format("%s-%s", repository, name);
    }

    private void addToBuffer(String key, RepositoryRecord record) {
        synchronized (this) {
            if (!buffer.containsKey(key)) {
                buffer.put(key, new ConcurrentHashMap<>());
            }

            Map<Integer, RepositoryRecord> map = buffer.get(key);

            synchronized (map) {
                map.put(record.hashCode(), record);
            }
        }
    }

    private synchronized void addBuffered(Notification notification) {
        String key = notification.getFullName();

        if (buffer.containsKey(key) && configGraph.containsKey(key)) {
            Map<Integer, RepositoryRecord> bufferedMap = buffer.get(key);
            ConfigGraphNode configGraphNode = configGraph.get(key);

            synchronized (bufferedMap) {
                bufferedMap.forEach((Integer index, RepositoryRecord record) -> configGraphNode.putRepository(record));
                buffer.remove(key);
            }
        }
    }
}
