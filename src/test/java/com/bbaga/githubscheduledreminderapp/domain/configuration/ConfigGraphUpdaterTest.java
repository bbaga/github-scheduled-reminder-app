package com.bbaga.githubscheduledreminderapp.domain.configuration;

import com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling.NotificationJobScheduler;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.repositories.GitHubInstallationRepository;
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
        ConfigGraphUpdater configUpdater = new ConfigGraphUpdater(installationRepository, configGraph, jobScheduler);

        Notification notification = new Notification("name", "type", new SlackNotificationConfiguration("* * * * * *"));

        assertEquals(0, configGraph.size());

        configUpdater.updateEntry(notification, 12, "some/repo", Instant.now());
        Mockito.verify(jobScheduler, Mockito.times(1)).upsertSchedule(notification);

        assertEquals(1, configGraph.size());
        assertSame(notification, configGraph.get("some/repo-name").getNotification());
        assertEquals(0, configUpdater.getBuffer().size());
    }

    @Test
    void updateEntryWithBufferUsage() throws SchedulerException {
        GitHubInstallationRepository installationRepository = Mockito.mock(GitHubInstallationRepository.class);
        ConcurrentHashMap<String, ConfigGraphNode> configGraph = new ConcurrentHashMap<>();
        NotificationJobScheduler jobScheduler = Mockito.mock(NotificationJobScheduler.class);
        ConfigGraphUpdater configUpdater = new ConfigGraphUpdater(installationRepository, configGraph, jobScheduler);

        Notification notification = new Notification("name", "type", new SlackNotificationConfiguration("* * * * * *"));
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
        Mockito.verify(jobScheduler, Mockito.times(1)).upsertSchedule(notification);

        assertSame(notification, configGraph.get("some/repo-name").getNotification());
        assertEquals(1, configGraph.get("some/repo-name").getRepositories().size());
        assertEquals(0, configUpdater.getBuffer().size());
    }

    @Test
    void clearOutdated() {
    }

    @Test
    void testClearOutdated() {
    }

    @Test
    void removeRepository() {
    }
}