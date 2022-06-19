package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.stereotype.Service;

@Service
public class ChannelMessageDeleteQueue {
  private ConcurrentLinkedQueue<Item> queue;

  public ChannelMessageDeleteQueue() {
    this.queue = new ConcurrentLinkedQueue<>();
  }
  public ChannelMessageDeleteQueue(ConcurrentLinkedQueue<Item> queue) {
    this.queue = queue;
  }

  public Item take() {
    return queue.poll();
  }

  public boolean isEmpty() {
    return queue.isEmpty();
  }

  public void put(Item item) {
    queue.add(item);
  }

  public static class Item {
    private String messageId;
    private String channelId;

    public Item(String messageId, String channelId) {
      this.messageId = messageId;
      this.channelId = channelId;
    }

    public String getMessageId() {
      return messageId;
    }

    public String getChannelId() {
      return channelId;
    }
  }
}
