package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import static org.junit.jupiter.api.Assertions.*;

import com.slack.api.methods.request.search.SearchMessagesRequest;
import com.slack.api.model.MatchedItem;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

class SearchMessageQueueItemTest {

  @Test
  void testEquals() {
    var paramsA = SearchMessagesRequest.builder().count(10).query("from:channel").build();
    var paramsB = SearchMessagesRequest.builder().count(10).query("from:channel").build();
    Consumer<MatchedItem> consumer = m -> {};

    assertEquals(new SearchMessageQueueItem(paramsA, consumer), new SearchMessageQueueItem(paramsB, consumer));
  }
}
