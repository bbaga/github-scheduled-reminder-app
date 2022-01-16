package com.bbaga.githubscheduledreminderapp.infrastructure.configuration;

import com.bbaga.githubscheduledreminderapp.domain.configuration.*;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.*;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.DraftFilterConfig;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class InRepoSlackRealTimeConfigParserTest {

    @Test
    void parseScheduleConfigTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        String configPath = "path/to/config.yaml";
        String config = """
enabled: true
notifications:
  - name: real-time
    type: slack/real-time/user
    config:
      sources:
        - type: event-pr-review-requested
          actions:
            - opened
          reviewers:
            - username
          teams:
            - org/team
          filters:
            - type: draft-filter
      user-name-map:
        git-user: slack-user
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
        SlackRealTimeUserNotificationConfiguration slackConfig = (SlackRealTimeUserNotificationConfiguration)notification.getConfig();

        Assertions.assertEquals("real-time", notification.getName());
        Assertions.assertEquals("slack/real-time/user", notification.getType());
        Assertions.assertEquals("slack-user", slackConfig.getUserNameMap().get("git-user"));
    }
}
