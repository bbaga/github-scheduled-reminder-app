package com.bbaga.githubscheduledreminderapp.domain.configuration;

import com.bbaga.githubscheduledreminderapp.domain.configuration.configGraphUpdater.*;
import com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling.NotificationJobScheduler;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.repositories.GitHubInstallationRepository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.quartz.SchedulerException;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class ConfigGraphUpdaterTest {

    @Test
    void updateEntry() throws SchedulerException {
        GitHubInstallationRepository installationRepository = Mockito.mock(GitHubInstallationRepository.class);
        ConcurrentHashMap<String, ConfigGraphNode> configGraph = new ConcurrentHashMap<>();
        NotificationJobScheduler jobScheduler = Mockito.mock(NotificationJobScheduler.class);
        ConfigVisitorFactoryFactory configVisitorFactoryFactory = new ConfigVisitorFactoryFactory(installationRepository);
        ConfigGraphUpdater configUpdater = new ConfigGraphUpdater(configVisitorFactoryFactory, configGraph, jobScheduler);

        SlackNotificationConfiguration config = new SlackNotificationConfiguration("* * * * * *");
        Notification notification = new Notification("name", "type", config);

        assertEquals(0, configGraph.size());

        configUpdater.updateEntry(notification, 12, "some/repo", Instant.now());
        Mockito.verify(jobScheduler, Mockito.times(1)).upsertSchedule(notification.getFullName(), config);

        assertEquals(1, configGraph.size());
        assertSame(notification, configGraph.get("some/repo-name").getNotification());
        assertEquals(0, configUpdater.getBuffer().size());
    }

    @Test
    void updateEntryWithBufferUsage() throws SchedulerException {
        GitHubInstallationRepository installationRepository = Mockito.mock(GitHubInstallationRepository.class);
        ConcurrentHashMap<String, ConfigGraphNode> configGraph = new ConcurrentHashMap<>();
        NotificationJobScheduler jobScheduler = Mockito.mock(NotificationJobScheduler.class);
        ConfigVisitorFactoryFactory configVisitorFactoryFactory = new ConfigVisitorFactoryFactory(installationRepository);
        ConfigGraphUpdater configUpdater = new ConfigGraphUpdater(configVisitorFactoryFactory, configGraph, jobScheduler);

        SlackNotificationConfiguration config = new SlackNotificationConfiguration("* * * * * *");

        Notification notification = new Notification("name", "type", config);
        notification.setRepository("some/repo");
        Extending.MainConfig extendingConfig = new Extending.MainConfig();
        extendingConfig.setRepository("some/repo");
        extendingConfig.setName("name");
        Extending extending = new Extending();
        extending.setExtending(extendingConfig);

        assertEquals(0, configGraph.size());
        assertEquals(0, configUpdater.getBuffer().size());

        configUpdater.updateEntry(extending, 12, "some/other-repo", Instant.now());

        assertEquals(0, configGraph.size());
        assertEquals(1, configUpdater.getBuffer().size());

        configUpdater.updateEntry(notification, 12, "some/repo", Instant.now());
        Mockito.verify(jobScheduler, Mockito.times(1)).upsertSchedule(notification.getFullName(), config);

        assertSame(notification, configGraph.get("some/repo-name").getNotification());
        assertEquals(1, configGraph.get("some/repo-name").getRepositories().size());
        assertEquals(0, configUpdater.getBuffer().size());
    }

    @Test
    void clearOutdated() {
        Instant instantA = Instant.now();
        Instant instantB = Instant.now().minusSeconds(5);

        /**
         * Test subject for repositories from different installations, but both defined locally
         * Expected to have both repositories in the notification after clearing
         */
        ConfigGraphNode nodeA = nodeBuilder(
            "A",
            1,
            instantA,
            Map.of(
                "same-installation-id", new NotificationConfiguration(),
                "not-the-same-installation-id", new NotificationConfiguration()
            ),
            List.of(
                new RepositoryRecord("same-installation-id", 1L, instantA, new NotificationConfiguration()),
                new RepositoryRecord("not-the-same-installation-id", 2L, instantA, new NotificationConfiguration())
            )
        );

        /**
         * Test subject for repositories from different installations, but only one is defined locally
         * Expected to lose the repository that isn't defined locally
         */
        ConfigGraphNode nodeB = nodeBuilder(
            "B",
            1,
            instantA,
            Map.of(
                "same-installation-id", new NotificationConfiguration()
            ),
            List.of(
                new RepositoryRecord("same-installation-id", 1L, instantA, new NotificationConfiguration()),
                new RepositoryRecord("not-the-same-installation-id", 2L, instantA, new NotificationConfiguration())
            )
        );

        /**
         * Test subject for notification with wrong timestamp
         * Expected to lose the whole notification
         */
        ConfigGraphNode nodeC = nodeBuilder(
            "C",
            1,
            instantB,
            Map.of(
                "same-installation-id", new NotificationConfiguration()
            ),
            List.of(
                new RepositoryRecord("same-installation-id", 1L, instantA, new NotificationConfiguration()),
                new RepositoryRecord("not-the-same-installation-id", 2L, instantA, new NotificationConfiguration())
            )
        );

        /**
         * Test subject for repositories from different installations, none of them is defined locally
         * Expected to lose repositories:
         *  - not-the-same-installation-id-matching-ts, due to installation run TS and last seen TS mismatch
         *  - same-installation-id-wrong-ts, due to installation run TS and last seen TS mismatch
         */
        ConfigGraphNode nodeD = nodeBuilder(
            "D",
            2,
            instantB,
            Map.of(),
            List.of(
                new RepositoryRecord("same-installation-id-wrong-ts", 2L, instantA, new NotificationConfiguration()),
                new RepositoryRecord("same-installation-id-matching-ts", 2L, instantB, new NotificationConfiguration()),
                new RepositoryRecord("not-the-same-installation-id-wrong-ts", 1L, instantA, new NotificationConfiguration()),
                new RepositoryRecord("not-the-same-installation-id-matching-ts", 1L, instantB, new NotificationConfiguration())
            )
        );

        ConcurrentHashMap<String, ConfigGraphNode> configGraph = new ConcurrentHashMap<>();
        configGraph.put("A", nodeA);
        configGraph.put("B", nodeB);
        configGraph.put("C", nodeC);
        configGraph.put("D", nodeD);

        GitHubInstallationRepository installationRepository = Mockito.mock(GitHubInstallationRepository.class);
        NotificationJobScheduler jobScheduler = Mockito.mock(NotificationJobScheduler.class);
        ConfigVisitorFactoryFactory configVisitorFactoryFactory = new ConfigVisitorFactoryFactory(installationRepository);
        ConfigGraphUpdater configUpdater = new ConfigGraphUpdater(configVisitorFactoryFactory, configGraph, jobScheduler);
        configUpdater.clearOutdated(1L, instantA);
        configUpdater.clearOutdated(2L, instantB);

        assertEquals(3, configGraph.size());
        assertEquals(2, configGraph.get("A").getRepositories().size());
        assertEquals(1, configGraph.get("B").getRepositories().size());
        assertEquals(2, configGraph.get("D").getRepositories().size());
    }

    private ConfigGraphNode nodeBuilder(
        String name,
        long installation,
        Instant instant,
        Map<String, NotificationConfigurationInterface> repoConfig,
        List<RepositoryRecord> repoRecords
    ) {
        SlackNotificationConfiguration slackNotificationConfiguration = new SlackNotificationConfiguration();
        slackNotificationConfiguration.setRepositories(repoConfig);
        Notification notification = new Notification(name, "slack/scheduled/channel", slackNotificationConfiguration);
        ConfigGraphNode node = new ConfigGraphNode(installation, notification, instant);
        repoRecords.forEach(node::putRepository);

        return node;
    }

    @Test
    void removeRepository() {
    }
}