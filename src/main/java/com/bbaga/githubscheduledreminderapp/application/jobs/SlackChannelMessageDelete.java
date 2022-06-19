package com.bbaga.githubscheduledreminderapp.application.jobs;

import com.bbaga.githubscheduledreminderapp.domain.notifications.slack.ChannelMessageDeleteQueue;
import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.methods.params.chat.ChatDeleteParams;
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
    private final ChannelMessageDeleteQueue channelMessageDeleteQueue;
    private final SlackClient slackClient;

    public SlackChannelMessageDelete(
        ChannelMessageDeleteQueue channelMessageDeleteQueue,
        @Qualifier("slack.user") SlackClient slackClient
    ) {
        this.channelMessageDeleteQueue = channelMessageDeleteQueue;
        this.slackClient = slackClient;
    }

    public void execute(JobExecutionContext context) {
        logger.info("Starting Slack channel clean up job");

        var limit = 50;

        while (!channelMessageDeleteQueue.isEmpty() && limit-- > 0) {
            try {
                var item = channelMessageDeleteQueue.take();

                var params = ChatDeleteParams.builder()
                    .setChannelId(item.getChannelId())
                    .setMessageToDeleteTs(item.getMessageId())
                    .setAsUser(true)
                    .build();

                logger.info("Deleting  - Channel: "+item.getChannelId()+" Message ID: "+item.getMessageId());
                slackClient.deleteMessage(params);
            } catch (Exception exception) {
                logger.error(exception.getLocalizedMessage());
            }
        }

        logger.info("Finished Slack channel clean up job");
    }
}
