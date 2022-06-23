package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.hubspot.slack.client.methods.params.search.SearchMessagesParams;
import com.hubspot.slack.client.models.Message;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import org.springframework.stereotype.Service;

@Service
public class SearchMessageQueue {
  private ConcurrentLinkedQueue<Item> queue;

  public SearchMessageQueue() {
    this.queue = new ConcurrentLinkedQueue<>();
  }
  public SearchMessageQueue(ConcurrentLinkedQueue<Item> queue) {
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
    private SearchMessagesParams params;

    private Consumer<Message> action;

    public Item(SearchMessagesParams params, Consumer<Message> action) {
      this.params = params;
      this.action = action;
    }

    public SearchMessagesParams getParams() {
      return params;
    }

    public Consumer<Message> getAction() {
      return action;
    }
  }
}
