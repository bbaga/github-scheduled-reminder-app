package com.bbaga.githubscheduledreminderapp.application.jobs;

import com.bbaga.githubscheduledreminderapp.domain.notifications.slack.BoundedUniqueQueue;
import com.bbaga.githubscheduledreminderapp.domain.notifications.slack.ChannelMessageDeleteQueueItem;
import com.bbaga.githubscheduledreminderapp.domain.notifications.slack.SearchMessageQueueItem;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatDeleteRequest;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
public class SlackChannelMessageDelete implements Job {

    private final Logger logger = LoggerFactory.getLogger(SlackChannelMessageDelete.class);
    private final BoundedUniqueQueue<SearchMessageQueueItem> searchMessageQueue;
    private final BoundedUniqueQueue<ChannelMessageDeleteQueueItem> channelMessageDeleteQueue;
    private final MethodsClient slackAppClient;
    private final MethodsClient slackUserClient;

    public SlackChannelMessageDelete(
        BoundedUniqueQueue<SearchMessageQueueItem> searchMessageQueue,
        BoundedUniqueQueue<ChannelMessageDeleteQueueItem> channelMessageDeleteQueue,
        @Qualifier("slack.app") MethodsClient slackAppClient,
        @Qualifier("slack.user") MethodsClient slackUserClient
    ) {
        this.searchMessageQueue = searchMessageQueue;
        this.channelMessageDeleteQueue = channelMessageDeleteQueue;
        this.slackAppClient = slackAppClient;
        this.slackUserClient = slackUserClient;
    }

    public void execute(JobExecutionContext context) {
        logger.info("Starting Slack channel clean up job");

        logger.info("Searching messages...");
        searchAndAddMessagesToDeleteQueue();

        logger.info("Deleting messages messages...");
        processDeleteQueue();

        logger.info("Finished Slack channel clean up job");
    }

    private void processDeleteQueue() {
        // Magic number for Slack's chat.delete resource rate limit. https://api.slack.com/methods/chat.delete
        var limit = 50;

        while (!channelMessageDeleteQueue.isEmpty() && limit-- > 0) {
            try {
                var item = channelMessageDeleteQueue.take();

                var params = ChatDeleteRequest.builder()
                    .channel(item.getChannelId())
                    .ts(item.getMessageId())
                    .build();

                logger.info(
                    "Deleting: [Channel: " + item.getChannelId() + ", Message ID: " + item.getMessageId() + "]"
                );

                slackAppClient.chatDelete(params);
            } catch (Exception exception) {
                logger.error(exception.getLocalizedMessage());
            }
        }
    }

    private void searchAndAddMessagesToDeleteQueue() {
        // Magic number for Slack's search.messages resource rate limit. https://api.slack.com/methods/search.messages
        var limit = 20;

        while (!searchMessageQueue.isEmpty() && limit-- > 0) {
            try {
                var item = searchMessageQueue.take();
                logger.info("Searching messages with: \"" + item.getParams().getQuery()+ "\"");
                var results = slackUserClient.searchMessages(item.getParams());

                if (!results.isOk()) {
                    logger.error(results.getError());
                }

                results.getMessages().getMatches().forEach(item.getAction());
            } catch (Exception exception) {
                logger.error(exception.getLocalizedMessage());
            }
        }
    }
}
