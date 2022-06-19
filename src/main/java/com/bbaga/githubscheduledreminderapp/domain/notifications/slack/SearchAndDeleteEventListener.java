package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.bbaga.githubscheduledreminderapp.application.jobs.SlackChannelMessageDelete;
import com.hubspot.slack.client.SlackClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class SearchAndDeleteEventListener implements ApplicationListener<SearchAndDeleteEvent> {

    private final SlackClient slackClient;
    private final ChannelMessageDeleteQueue channelMessageDeleteQueue;

    private final Logger logger = LoggerFactory.getLogger(SearchAndDeleteEventListener.class);

    public SearchAndDeleteEventListener(SlackClient slackClient, ChannelMessageDeleteQueue channelMessageDeleteQueue) {
        this.slackClient = slackClient;
        this.channelMessageDeleteQueue = channelMessageDeleteQueue;
    }
    @Override
    public void onApplicationEvent(SearchAndDeleteEvent event) {
        if (slackClient == null) {
            return;
        }

        var results = slackClient.searchMessages(event.getSearchMessagesParams()).join();

        if (results.isOk()) {
            var message = results.unwrapOrElseThrow().getMessages();
            message.getMatches().forEach(
                m -> {
                    var item = new ChannelMessageDeleteQueue.Item(m.getTimestamp(), m.getChannel().getId());
                    try {
                        channelMessageDeleteQueue.put(item);
                    } catch (Exception exception) {
                        logger.error(exception.getLocalizedMessage());
                    }
                }
            );
        }
    }
}
