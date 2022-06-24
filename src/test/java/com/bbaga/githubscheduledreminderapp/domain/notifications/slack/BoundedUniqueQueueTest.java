package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class BoundedUniqueQueueTest {

  @Test
  void testQueueMethods() {
    var ts = String.valueOf(Instant.now().toEpochMilli());
    var firstItem = new ChannelMessageDeleteQueueItem(ts, "channel");
    var secondItem = new ChannelMessageDeleteQueueItem(ts, "channel2");
    var queue = new BoundedUniqueQueue<>();

    assertTrue(queue.isEmpty());

    queue.put(firstItem);
    queue.put(firstItem);
    queue.put(secondItem);

    assertEquals(firstItem, queue.take());
    assertEquals(secondItem, queue.take());

    assertTrue(queue.isEmpty());
  }

  @Test
  void testUpperBound() {
    var limit = 5;
    var queue = new BoundedUniqueQueue<>(limit);
    var ts = String.valueOf(Instant.now().toEpochMilli());

    for (var i = 0; i < limit + 6; i++) {
      queue.put(new ChannelMessageDeleteQueueItem(ts, "channel" + i));
    }

    assertEquals(limit, queue.size());

    queue.put(new ChannelMessageDeleteQueueItem(ts, "channelLast"));

    assertEquals(limit, queue.size());

  }
}
