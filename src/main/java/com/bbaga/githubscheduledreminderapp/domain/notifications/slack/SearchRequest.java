package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.bbaga.githubscheduledreminderapp.domain.configuration.template.TemplateConfig;
import java.util.Objects;

public class SearchRequest {
  private String notificationId;
  private String channel;
  private String channelId;
  private String latest;
  private String botId;
  private TemplateConfig templateConfig;

  public SearchRequest(String botId, String notificationId, String channel, String channelId, String latest, TemplateConfig templateConfig) {
    this.botId = botId;
    this.notificationId = notificationId;
    this.channelId = channelId;
    this.channel = channel;
    this.latest = latest;
    this.templateConfig = templateConfig;
  }

  public String getBotId() {
    return botId;
  }

  public String getNotificationId() {
    return notificationId;
  }

  public String getChannel() {
    return channel;
  }

  public String getChannelId() {
    return channelId;
  }

  public String getLatest() {
    return this.latest;
  }

  public TemplateConfig getTemplateConfig() {
    return templateConfig;
  }

  public static SearchRequest create(
      String botId,
      String notificationId,
      String channel,
      String channelId,
      String latest,
      TemplateConfig templateConfig
  ) {
    return new SearchRequest(
      botId,
      notificationId,
      channel,
      channelId,
      latest,
      templateConfig
    );
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SearchRequest that)) {
      return false;
    }
    return getNotificationId().equals(that.getNotificationId())
        && getChannelId().equals(that.getChannelId())
        && getLatest().equals(that.getLatest())
        && getBotId().equals(that.getBotId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        getNotificationId(),
        getChannelId(),
        getLatest(),
        getBotId()
    );
  }
}
