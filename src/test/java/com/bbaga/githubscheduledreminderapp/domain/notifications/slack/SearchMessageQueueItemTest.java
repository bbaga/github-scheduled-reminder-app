package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import static org.junit.jupiter.api.Assertions.*;

import com.hubspot.slack.client.methods.params.search.SearchMessagesParams;
import com.hubspot.slack.client.models.Message;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

class SearchMessageQueueItemTest {

  @Test
  void testEquals() {
    var paramsA = SearchMessagesParams.builder().setCount(10).setQuery("from:channel").build();
    var paramsB = SearchMessagesParams.builder().setCount(10).setQuery("from:channel").build();
    Consumer<Message> consumer = m -> {};

    assertEquals(new SearchMessageQueueItem(paramsA, consumer), new SearchMessageQueueItem(paramsB, consumer));
  }
}
