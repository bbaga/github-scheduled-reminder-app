package com.bbaga.githubscheduledreminderapp.application.config;

import com.bbaga.githubscheduledreminderapp.domain.notifications.slack.ChannelMessageDeleteQueue;
import com.bbaga.githubscheduledreminderapp.domain.notifications.slack.SearchAndDeleteEventListener;
import com.bbaga.githubscheduledreminderapp.domain.statistics.AggregatedStatisticsStorage;
import com.bbaga.githubscheduledreminderapp.domain.statistics.StatisticsEventListener;
import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.SlackClientFactory;
import com.hubspot.slack.client.SlackClientRuntimeConfig;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackClientConfig {
  @Value("${application.slack.apiToken}")
  private String slackApiToken;

  @Value("${application.slack.apiTokenFile}")
  private String slackApiTokenFile;

  @Value("${application.slack.apiUserToken}")
  private String slackApiUserToken;

  @Value("${application.slack.apiUserTokenFile}")
  private String slackApiUserTokenFile;

  private static final Logger log = LoggerFactory.getLogger(Config.class);

  @Bean
  @Qualifier("slack.app")
  public SlackClient slackApp() throws Exception {
    if (!slackApiTokenFile.isEmpty() && slackApiToken.isEmpty()) {
      try(FileInputStream inputStream = new FileInputStream(slackApiTokenFile)) {
        slackApiToken = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
      }
    }

    if (slackApiToken.isEmpty()) {
      throw new IllegalStateException("Slack API Token must be configured, see the SLACK_API_TOKEN and SLACK_API_TOKEN_FILE environment variables.");
    }

    SlackClientRuntimeConfig runtimeConfig = SlackClientRuntimeConfig.builder()
        .setTokenSupplier(() -> slackApiToken)
        .build();

    return SlackClientFactory.defaultFactory().build(runtimeConfig);
  }

  @Bean
  @Qualifier("slack.user")
  public SlackClient slackUser() throws Exception {
    if (!slackApiUserTokenFile.isEmpty() && slackApiUserToken.isEmpty()) {
      try(FileInputStream inputStream = new FileInputStream(slackApiUserTokenFile)) {
        slackApiUserToken = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
      }
    }

    if (slackApiUserToken.isEmpty()) {
      log.info("Slack User API Client is disabled. To enable it, provide a User token through the SLACK_API_USER_TOKEN or SLACK_API_USER_TOKEN_FILE environment variables.");
      return null;
    }

    SlackClientRuntimeConfig runtimeConfig = SlackClientRuntimeConfig.builder()
        .setTokenSupplier(() -> slackApiUserToken)
        .build();

    return SlackClientFactory.defaultFactory().build(runtimeConfig);
  }


  @Bean
  public SearchAndDeleteEventListener getSearchAndDeleteEventListener(
      @Qualifier("slack.user") SlackClient slackClient,
      ChannelMessageDeleteQueue channelMessageDeleteQueue
  ) {
    return new SearchAndDeleteEventListener(slackClient, channelMessageDeleteQueue);
  }
}
