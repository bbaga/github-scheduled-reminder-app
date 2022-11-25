package com.bbaga.githubscheduledreminderapp.application.jobs;

import com.bbaga.githubscheduledreminderapp.domain.notifications.slack.BoundedUniqueQueue;
import com.bbaga.githubscheduledreminderapp.domain.notifications.slack.ChannelMessageDeleteQueueItem;
import com.bbaga.githubscheduledreminderapp.domain.notifications.slack.SearchMessageQueueItem;
import com.bbaga.githubscheduledreminderapp.domain.notifications.slack.SearchRequest;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatDeleteRequest;
import com.slack.api.methods.request.conversations.ConversationsHistoryRequest;
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse;
import com.slack.api.model.Message;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.MarkdownTextObject;
import java.io.IOException;
import java.util.Optional;
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

    public SlackChannelMessageDelete(
        BoundedUniqueQueue<SearchMessageQueueItem> searchMessageQueue,
        BoundedUniqueQueue<ChannelMessageDeleteQueueItem> channelMessageDeleteQueue,
        @Qualifier("slack.app") MethodsClient slackAppClient
    ) {
        this.searchMessageQueue = searchMessageQueue;
        this.channelMessageDeleteQueue = channelMessageDeleteQueue;
        this.slackAppClient = slackAppClient;
    }

    public void execute(JobExecutionContext context) {
        logger.debug("Starting Slack channel clean up job");

        logger.debug("Searching messages...");
        searchAndAddMessagesToDeleteQueue();

        logger.debug("Deleting messages messages...");
        processDeleteQueue();

        logger.debug("Finished Slack channel clean up job");
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
        // Magic number for Slack's conversations.history resource rate limit. https://api.slack.com/methods/conversations.history
        var limit = 50;

        while (!searchMessageQueue.isEmpty() && limit-- > 0) {
            try {
                var hasMore = false;
                Optional<String> cursor = Optional.empty();
                var item = searchMessageQueue.take();
                var params = item.getParams();

                logger.info(
                    String.format(
                        "Searching messages on \"#%s\"with notification id \"%s\"",
                        params.getChannel(),
                        params.getNotificationId()
                    )
                );

                // Limit the search to 5 pages of history per configuration
                var paginationLimit = 5;

                /**
                 * Notifications can be split into multiple messages
                 * `splitMessageMatch` will remember to queue messages to be deleted when a message "end" was found.
                 * Will be reset to `false` when a "main header" string was found.
                 */
                var splitMessageMatch = false;
                do {
                    ConversationsHistoryResponse results = getConversationsHistoryPageResponse(cursor, params);

                    if (!results.isOk()) {
                        logger.error(results.getError());
                        continue;
                    }

                    // Iterating over the messages found on the page
                    for (Message message : results.getMessages()) {
                        var match = false;

                        if (!isMessageRelevant(message, params.getBotId())) {
                            continue;
                        }

                        var blocks = message.getBlocks();
                        var lastBlock = blocks.get(blocks.size()-1);

                        // Matching notification in a block layout
                        match = matchBlockLayoutMessage(params, lastBlock);

                        // Matching notification in a markdown/rich_text block
                        if (lastBlock.getType().equals("rich_text")) {
                            var text = message.getText();
                            match = text.contains(params.getNotificationId());

                            // If the "main header" is not present in the text, we are dealing with a split message
                            if (text.startsWith(params.getTemplateConfig().getHeaderMain())) {
                                match = match || splitMessageMatch;
                                splitMessageMatch = false;
                            } else if (match) {
                                splitMessageMatch = true;
                            }
                        }

                        /**
                         * Queue messages when the config id was found in a block layout
                         * or when a long notification was split into multiple messages, all messages between the last
                         * and the first chunk should be deleted
                         */
                        if (match || splitMessageMatch) {
                            message.setChannel(params.getChannelId());
                            item.getAction().accept(message);
                        }
                    }

                    hasMore = results.isHasMore();
                    if (hasMore) {
                        cursor = Optional.of(results.getResponseMetadata().getNextCursor());
                    }
                } while (hasMore && --limit > 0 && (--paginationLimit > 0 || splitMessageMatch));
            } catch (Exception exception) {
                logger.error(exception.getLocalizedMessage());
            }
        }
    }

    private static boolean matchBlockLayoutMessage(SearchRequest params, LayoutBlock lastBlock) {
        if (!(lastBlock instanceof SectionBlock)) {
            return false;
        }

        var lastSectionBlock = (SectionBlock) lastBlock;
        if (lastSectionBlock.getText() instanceof MarkdownTextObject) {
            return lastSectionBlock.getText().getText().contains(params.getNotificationId());
        }

        return false;
    }

    private static boolean isMessageRelevant(Message message, String botId) {
        if (message.getBotId() == null) {
            return false;
        }

        if (!message.getBotId().equals(botId)) {
            return false;
        }

        if (message.getBlocks().isEmpty()) {
            return false;
        }

        return true;
    }

    private ConversationsHistoryResponse getConversationsHistoryPageResponse(Optional<String> cursor, SearchRequest params) throws IOException, SlackApiException {
        var request = ConversationsHistoryRequest.builder()
            .channel(params.getChannelId())
            .limit(150)
            .latest(params.getLatest());

        cursor.ifPresent(request::cursor);
        return slackAppClient.conversationsHistory(request.build());
    }
}
