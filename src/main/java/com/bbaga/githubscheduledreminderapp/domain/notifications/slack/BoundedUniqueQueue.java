package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.stereotype.Service;

@Service
public class BoundedUniqueQueue<T> {
  private ConcurrentLinkedQueue<T> queue;

  private int limit = 500;

  public BoundedUniqueQueue() {
    this.queue = new ConcurrentLinkedQueue<>();
  }

  public BoundedUniqueQueue(ConcurrentLinkedQueue<T> queue) {
    this.queue = queue;
  }

  public BoundedUniqueQueue(int limit) {
    this.queue = new ConcurrentLinkedQueue<>();
    this.limit = limit;
  }

  public int size() {
    return queue.size();
  }

  public T take() {
    return this.queue.poll();
  }

  public boolean isEmpty() {
    return queue.isEmpty();
  }

  public void put(T item) {
    if (queue.contains(item) || queue.size() >= limit) {
      return;
    }

    queue.add(item);
  }
}
