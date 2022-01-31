package com.bbaga.githubscheduledreminderapp.application.jobs;

import com.bbaga.githubscheduledreminderapp.domain.GitHubAppInstallationService;
import com.bbaga.githubscheduledreminderapp.domain.configuration.ConfigGraphNode;
import com.bbaga.githubscheduledreminderapp.domain.notifications.NotificationStrategyInterface;
import com.bbaga.githubscheduledreminderapp.domain.notifications.slack.ChannelNotification;
import com.bbaga.githubscheduledreminderapp.domain.notifications.slack.NotificationStrategy;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Component
public class ScheduledNotification implements Job, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private GitHubAppInstallationService appInstallationService;

    @Autowired
    @Qualifier("ConfigGraph")
    private ConcurrentHashMap<String, ConfigGraphNode> configGraph;

    private final Logger logger = LoggerFactory.getLogger(ScheduledNotification.class);
    private final Map<String, Class<?>> strategyNameToClassMap = Map.of(
        "slack/channel", NotificationStrategy.class,
        "slack/scheduled/channel", NotificationStrategy.class
    );

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void execute(JobExecutionContext context) {
        String jobName = context.getJobDetail().getKey().getName();
        logger.info("Starting notification job for {}", jobName);

        ConfigGraphNode node = configGraph.get(jobName);
        try {
            getStrategy(node.getNotification().getType()).sendNotification(node);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        logger.info("Finished notification job for {}", jobName);
    }

    private NotificationStrategyInterface getStrategy(String name) {
        if (this.strategyNameToClassMap.containsKey(name)) {
            return this.getStrategyFactory(this.strategyNameToClassMap.get(name)).get();
        }

        throw new RuntimeException(String.format("Notification delivery type \"%s\" is not available", name));
    }

    private Supplier<NotificationStrategyInterface> getStrategyFactory(Class<?> cls) {
        final HashMap<Class<?>, Supplier<NotificationStrategyInterface>> strategyClassToFactoryMap = new HashMap<>();

        strategyClassToFactoryMap.put(
            NotificationStrategy.class,
            () -> NotificationStrategy.create(appInstallationService, this.applicationContext.getBean(ChannelNotification.class))
        );

        if (strategyClassToFactoryMap.containsKey(cls)) {
            return strategyClassToFactoryMap.get(cls);
        }

        throw new RuntimeException(String.format("Notification delivery for type \"%s\" is not available", cls.getName()));
    }
}
