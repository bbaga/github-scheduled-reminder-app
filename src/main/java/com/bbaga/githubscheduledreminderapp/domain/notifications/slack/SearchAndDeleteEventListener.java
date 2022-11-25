package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.slack.api.model.Message;
import java.time.Instant;
import org.springframework.context.ApplicationListener;

public class SearchAndDeleteEventListener implements ApplicationListener<SearchAndDeleteEvent> {

    private final BoundedUniqueQueue<ChannelMessageDeleteQueueItem> channelMessageDeleteQueue;

    private final BoundedUniqueQueue<SearchMessageQueueItem> searchMessageQueue;

    public SearchAndDeleteEventListener(
        BoundedUniqueQueue<SearchMessageQueueItem> searchMessageQueue,
        BoundedUniqueQueue<ChannelMessageDeleteQueueItem> channelMessageDeleteQueue
    ) {
        this.searchMessageQueue = searchMessageQueue;
        this.channelMessageDeleteQueue = channelMessageDeleteQueue;
    }
    @Override
    public void onApplicationEvent(SearchAndDeleteEvent event) {
        var item = new SearchMessageQueueItem(event.getSearchRequest(), (Message m) -> {
            channelMessageDeleteQueue.put(
                new ChannelMessageDeleteQueueItem(m.getTs(), m.getChannel())
            );
        });
        searchMessageQueue.put(item);
    }
}
