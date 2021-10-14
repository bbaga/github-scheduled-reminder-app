package com.bbaga.githubscheduledreminderapp.jobs;

import com.bbaga.githubscheduledreminderapp.GitHubAppInstallationService;
import com.bbaga.githubscheduledreminderapp.configuration.ConfigGraphNode;
import com.bbaga.githubscheduledreminderapp.configuration.Notification;
import com.bbaga.githubscheduledreminderapp.configuration.configgraphnode.RepositoryRecord;
import com.bbaga.githubscheduledreminderapp.notifications.slack.ChannelNotification;
import com.bbaga.githubscheduledreminderapp.notifications.slack.ChannelNotificationDataProvider;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ScheduledSlackNotification implements Job {

    @Autowired
    private ChannelNotification channelNotification;

    @Autowired
    private GitHubAppInstallationService appInstallationService;

    @Autowired
    @Qualifier("ConfigGraph")
    private ConcurrentHashMap<String, ConfigGraphNode> configGraph;

    private final Logger logger = LoggerFactory.getLogger(ScheduledSlackNotification.class);

    public void execute(JobExecutionContext context) {
        String jobName = context.getJobDetail().getKey().getName();
        logger.info("Starting notification job for {}", jobName);

        channelNotification.send(
            new ChannelNotificationDataProvider(
                configGraph.get(jobName),
                appInstallationService
            ).getData()
        );

        logger.info("Finished notification job for {}", jobName);
    }
}
