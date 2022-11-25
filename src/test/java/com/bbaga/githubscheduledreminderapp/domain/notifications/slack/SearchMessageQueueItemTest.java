package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import static org.junit.jupiter.api.Assertions.*;

import com.bbaga.githubscheduledreminderapp.domain.configuration.template.TemplateConfig;
import com.slack.api.model.Message;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SearchMessageQueueItemTest {

  @Test
  void testEquals() {
    var config = Mockito.mock(TemplateConfig.class);
    var paramsA = SearchRequest.create(
        "botId",
        "notification/id",
        "myChannel",
        "C12345",
        "latest-ts",
        config
    );

    var paramsB = SearchRequest.create(
        "botId",
        "notification/id",
        "myChannel",
        "C12345",
        "latest-ts",
        config
    );

    Consumer<Message> consumer = m -> {};

    assertEquals(new SearchMessageQueueItem(paramsA, consumer), new SearchMessageQueueItem(paramsB, consumer));
  }
}
