package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import java.util.Objects;

public class ChannelMessageDeleteQueueItem {
  private final String messageId;
  private final String channelId;

  public ChannelMessageDeleteQueueItem(String messageId, String channelId) {
    this.messageId = messageId;
    this.channelId = channelId;
  }

  public String getMessageId() {
    return messageId;
  }

  public String getChannelId() {
    return channelId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof BoundedUniqueQueue.Item)) {
      return false;
    }

    BoundedUniqueQueue.Item item = (BoundedUniqueQueue.Item) o;
    return getMessageId().equals(item.getMessageId()) && getChannelId().equals(item.getChannelId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getMessageId(), getChannelId());
  }
}
