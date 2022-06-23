package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.hubspot.slack.client.models.Message;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class SearchAndDeleteEventListener implements ApplicationListener<SearchAndDeleteEvent> {

    private final ChannelMessageDeleteQueue channelMessageDeleteQueue;

    private final SearchMessageQueue searchMessageQueue;

    private final Logger logger = LoggerFactory.getLogger(SearchAndDeleteEventListener.class);

    public SearchAndDeleteEventListener(
        SearchMessageQueue searchMessageQueue,
        ChannelMessageDeleteQueue channelMessageDeleteQueue
    ) {
        this.searchMessageQueue = searchMessageQueue;
        this.channelMessageDeleteQueue = channelMessageDeleteQueue;
    }
    @Override
    public void onApplicationEvent(SearchAndDeleteEvent event) {
        var item = new SearchMessageQueue.Item(event.getSearchMessagesParams(), (Message m) -> {
            var timestampParts = m.getTimestamp().split("\\.");
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
                channelMessageDeleteQueue.put(new ChannelMessageDeleteQueue.Item(m.getTimestamp(), m.getChannel().getId()));
            }
        });
        searchMessageQueue.put(item);
    }
}
