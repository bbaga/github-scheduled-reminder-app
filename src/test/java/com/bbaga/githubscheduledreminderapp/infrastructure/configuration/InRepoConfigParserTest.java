package com.bbaga.githubscheduledreminderapp.infrastructure.configuration;

import com.bbaga.githubscheduledreminderapp.domain.configuration.Extending;
import com.bbaga.githubscheduledreminderapp.domain.configuration.Notification;
import com.bbaga.githubscheduledreminderapp.domain.configuration.SlackNotification;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class InRepoConfigParserTest {

    @Test
    void parseScheduleConfigTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        String configPath = "path/to/config.yaml";
        String config = """
enabled: true
notifications:
  - name: testing
    schedule: "0 1 2 3 4 5 6"
    type: slack/channel
    config:
      channel: "test-channel"
""";

        InputStream stream = new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8));

        GHContent content = Mockito.mock(GHContent.class);
        Mockito.doReturn(stream).when(content).read();

        GHRepository repository = Mockito.mock(GHRepository.class);
        Mockito.doReturn(content).when(repository).getFileContent(configPath);

        InRepoConfigParser parser = new InRepoConfigParser(mapper, configPath);

        InRepoConfig parsedConfig = parser.getFrom(repository);

        Assertions.assertEquals(true, parsedConfig.getEnabled());
        Assertions.assertEquals(1, parsedConfig.getNotifications().size());

        Notification notification = (Notification) parsedConfig.getNotifications().get(0);
        Assertions.assertEquals("testing", notification.getName());
        Assertions.assertEquals("0 1 2 3 4 5 6", notification.getSchedule().get());
        Assertions.assertEquals("UTC", notification.getTimeZone());
        Assertions.assertEquals("slack/channel", notification.getType());

        SlackNotification notificationConfig = (SlackNotification) notification.getConfig();
        Assertions.assertEquals("test-channel", notificationConfig.getChannel());
    }

    @Test
    void parseScheduleConfigWithTimeZoneTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        String configPath = "path/to/config.yaml";
        String config = """
enabled: true
notifications:      
  - name: timezone
    schedule: "* * * * * *"
    type: slack/channel
    timezone: EST
    config:
      channel: "dev-channel"
""";

        InputStream stream = new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8));

        GHContent content = Mockito.mock(GHContent.class);
        Mockito.doReturn(stream).when(content).read();

        GHRepository repository = Mockito.mock(GHRepository.class);
        Mockito.doReturn(content).when(repository).getFileContent(configPath);

        InRepoConfigParser parser = new InRepoConfigParser(mapper, configPath);

        InRepoConfig parsedConfig = parser.getFrom(repository);

        Assertions.assertEquals(true, parsedConfig.getEnabled());
        Assertions.assertEquals(1, parsedConfig.getNotifications().size());

        Notification notification = (Notification) parsedConfig.getNotifications().get(0);
        Assertions.assertEquals("timezone", notification.getName());
        Assertions.assertEquals("* * * * * *", notification.getSchedule().get());
        Assertions.assertEquals("EST", notification.getTimeZone());
        Assertions.assertEquals("slack/channel", notification.getType());

        SlackNotification notificationConfig = ((SlackNotification) notification.getConfig());
        Assertions.assertEquals("dev-channel", notificationConfig.getChannel());
    }

    @Test
    void parseExtensionConfigTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        String configPath = "path/to/config.yaml";
        String config = """
enabled: true
notifications:
  - extending:
      repository: some/repository
      name: something
""";

        InputStream stream = new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8));

        GHContent content = Mockito.mock(GHContent.class);
        Mockito.doReturn(stream).when(content).read();

        GHRepository repository = Mockito.mock(GHRepository.class);
        Mockito.doReturn(content).when(repository).getFileContent(configPath);

        InRepoConfigParser parser = new InRepoConfigParser(mapper, configPath);

        InRepoConfig parsedConfig = parser.getFrom(repository);

        Assertions.assertEquals(true, parsedConfig.getEnabled());
        Assertions.assertEquals(1, parsedConfig.getNotifications().size());

        Extending notification = (Extending) parsedConfig.getNotifications().get(0);
        Assertions.assertEquals("some/repository", notification.getExtending().getRepository());
        Assertions.assertEquals("something", notification.getExtending().getName());
    }

    @Test
    void parseExtensionConfigOverwriteTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        String configPath = "path/to/config.yaml";
        String config = """
enabled: true
notifications:
  - extending:
      repository: some/repository
      name: something
    config:
      channel: gijdskgfjsd
      sources:
        - type: issues
        - type: pull-requests
          filters:
            - type: draft-filter
              include-drafts: true
""";

        InputStream stream = new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8));

        GHContent content = Mockito.mock(GHContent.class);
        Mockito.doReturn(stream).when(content).read();

        GHRepository repository = Mockito.mock(GHRepository.class);
        Mockito.doReturn(content).when(repository).getFileContent(configPath);

        InRepoConfigParser parser = new InRepoConfigParser(mapper, configPath);

        InRepoConfig parsedConfig = parser.getFrom(repository);

        Assertions.assertEquals(true, parsedConfig.getEnabled());
        Assertions.assertEquals(1, parsedConfig.getNotifications().size());

        Extending notification = (Extending) parsedConfig.getNotifications().get(0);
        Assertions.assertEquals("some/repository", notification.getExtending().getRepository());
        Assertions.assertEquals("something", notification.getExtending().getName());
    }
}