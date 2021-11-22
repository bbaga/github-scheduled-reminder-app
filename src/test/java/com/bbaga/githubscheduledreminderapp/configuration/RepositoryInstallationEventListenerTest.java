package com.bbaga.githubscheduledreminderapp.configuration;

import com.bbaga.githubscheduledreminderapp.GitHubAppInstallationService;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.webhook.events.RepositoryInstallationEvent;
import com.bbaga.githubscheduledreminderapp.jobs.scheduling.InstallationScanJobScheduler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.quartz.SchedulerException;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryInstallationEventListenerTest {

    @Test
    void onInstallationAdded() throws IOException, SchedulerException {
        String payload = """
{
  "action": "added",
  "installation": {
    "id": 987654321,
    "account": {},
    "permissions": {},
    "events": [],
    "created_at": "2021-02-07T16:23:34.000+01:00",
    "updated_at": "2021-10-05T17:33:22.000+02:00",
    "single_file_name": ".demo-bot.yaml",
    "has_multiple_single_files": false,
    "single_file_paths": [],
    "suspended_by": null,
    "suspended_at": null
  },
  "repository_selection": "selected",
  "repositories_added": [
    {
      "id": 123456789,
      "node_id": "XYZ=",
      "name": "aerospike-client-php",
      "full_name": "bbaga/foo-bar",
      "private": false
    }
  ],
  "repositories_removed": [],
  "requester": null,
  "sender": {}
}
            """;

        ArrayList<Notification> notificationList = new ArrayList<>();
        notificationList.add(new Notification("testing", "* * * * * ?", "slack/channel", new HashMap<>()));

        InRepoConfig inRepoConfig = new InRepoConfig();
        inRepoConfig.setEnabled(true);
        inRepoConfig.setNotifications(notificationList);

        GHRepository mockRepository = Mockito.mock(GHRepository.class);
        Mockito.when(mockRepository.isArchived()).thenReturn(false);
        Mockito.when(mockRepository.getFullName()).thenReturn("bbaga/foo-bar");

        GitHub mockGitHubClient = Mockito.mock(GitHub.class);
        Mockito.when(mockGitHubClient.getRepository("bbaga/foo-bar")).thenReturn(mockRepository);

        ConfigGraphUpdater graphUpdater = Mockito.mock(ConfigGraphUpdater.class);
        InRepoConfigParser inRepoConfigParser = Mockito.mock(InRepoConfigParser.class);
        Mockito.when(inRepoConfigParser.getFrom(mockRepository)).thenReturn(inRepoConfig);

        GitHubAppInstallationService installationService = Mockito.mock(GitHubAppInstallationService.class);
        Mockito.when(installationService.getClientByInstallationId(987654321L)).thenReturn(mockGitHubClient);

        InstallationScanJobScheduler jobScheduler = Mockito.mock(InstallationScanJobScheduler.class);
        ObjectMapper jsonObjectMapper = JsonMapper.builder().findAndAddModules().build();

        RepositoryInstallationEventListener eventListener = new RepositoryInstallationEventListener(
            graphUpdater,
            inRepoConfigParser,
            jsonObjectMapper,
            installationService,
            jobScheduler
        );

        RepositoryInstallationEvent event = new RepositoryInstallationEvent(this, payload);

        eventListener.onApplicationEvent(event);

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        ArgumentCaptor<Long> installationIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> repoNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Instant> instantCaptor = ArgumentCaptor.forClass(Instant.class);

        Mockito.verify(graphUpdater).updateEntry(
                notificationCaptor.capture(),
                installationIdCaptor.capture(),
                repoNameCaptor.capture(),
                instantCaptor.capture()
        );

        Notification notification = notificationCaptor.getValue();
        assertEquals("testing", notification.getName());
        assertEquals("* * * * * ?", notification.getSchedule());

        Long installationId = installationIdCaptor.getValue();
        assertEquals(987654321L, installationId);

        String repoName = repoNameCaptor.getValue();
        assertEquals("bbaga/foo-bar", repoName);
    }

    @Test
    void onInstallationRemoved() throws IOException, SchedulerException {
        String payload = """
{
   "action": "removed",
   "installation": {
     "id": 987654321,
     "account": {},
     "repository_selection": "all",
     "permissions": {},
     "events": [],
     "created_at": "2021-02-07T15:23:34.000Z",
     "updated_at": "2021-11-22T09:59:44.000Z",
     "single_file_name": ".demo-bot.yaml",
     "has_multiple_single_files": false,
     "single_file_paths": [],
     "suspended_by": null,
     "suspended_at": null
   },
   "repository_selection": "selected",
   "repositories_added": [],
   "repositories_removed": [
     {
       "id": 123456789,
       "node_id": "XYZ==",
       "name": "amqp-base",
       "full_name": "bbaga/foo-bar",
       "private": false
     }
   ],
   "requester": null,
   "sender": {}
 }
                """;

        ConfigGraphUpdater graphUpdater = Mockito.mock(ConfigGraphUpdater.class);
        InRepoConfigParser inRepoConfigParser = Mockito.mock(InRepoConfigParser.class);
        GitHubAppInstallationService installationService = Mockito.mock(GitHubAppInstallationService.class);
        InstallationScanJobScheduler jobScheduler = Mockito.mock(InstallationScanJobScheduler.class);
        ObjectMapper jsonObjectMapper = JsonMapper.builder().findAndAddModules().build();

        RepositoryInstallationEventListener eventListener = new RepositoryInstallationEventListener(
            graphUpdater,
            inRepoConfigParser,
            jsonObjectMapper,
            installationService,
            jobScheduler
        );

        RepositoryInstallationEvent event = new RepositoryInstallationEvent(this, payload);

        eventListener.onApplicationEvent(event);

        ArgumentCaptor<Long> installationIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> repoNameCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(graphUpdater).removeRepository(
                installationIdCaptor.capture(),
                repoNameCaptor.capture()
        );

        Long installationId = installationIdCaptor.getValue();
        assertEquals(987654321L, installationId);

        String repoName = repoNameCaptor.getValue();
        assertEquals("bbaga/foo-bar", repoName);
    }
}
