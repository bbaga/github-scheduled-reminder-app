package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.slack.api.model.MatchedItem;
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
        var item = new SearchMessageQueueItem(event.getSearchMessagesParams(), (MatchedItem m) -> {
            var timestampParts = m.getTs().split("\\.");
            long seconds, nanoAdjustment;
            Instant messageTimestamp;

            if (timestampParts.length > 0) {
                seconds = Long.parseLong(timestampParts[0]);
                nanoAdjustment = timestampParts.length > 1 ? Long.parseLong(timestampParts[1]) : 0;

                messageTimestamp = Instant.ofEpochSecond(seconds, nanoAdjustment);
            } else {
                // how come there is nothing?
                return;
            }

            if (messageTimestamp.isBefore(event.getDeleteMessagesBefore())) {
                channelMessageDeleteQueue.put(
                    new ChannelMessageDeleteQueueItem(m.getTs(), m.getChannel().getId())
                );
            }
        });
        searchMessageQueue.put(item);
    }
}
