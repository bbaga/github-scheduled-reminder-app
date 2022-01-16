package com.bbaga.githubscheduledreminderapp.domain.configuration.configGraphUpdater;

import com.bbaga.githubscheduledreminderapp.domain.configuration.*;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.webhooks.PrReviewRequestedListener;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.repositories.GitHubInstallationRepository;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.webhook.EventPublisher;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ApplicationEventMulticaster;

import java.util.Map;

public class NotificationConfigVisitor {
    private final ConfigGraphUpdater configGraphUpdater;
    private final Notification notification;
    private final GitHubInstallationRepository installationRepository;
    private final ApplicationEventMulticaster eventMulticaster;
    private final EntryContext context;

    private final Logger logger = LoggerFactory.getLogger(NotificationConfigVisitor.class);

    public NotificationConfigVisitor(
        ConfigGraphUpdater configGraphUpdater,
        GitHubInstallationRepository installationRepository,
        Notification notification,
        ApplicationEventMulticaster eventMulticaster,
        EntryContext context
    ) {
        this.configGraphUpdater = configGraphUpdater;
        this.notification = notification;
        this.installationRepository = installationRepository;
        this.eventMulticaster = eventMulticaster;
        this.context = context;
    }

    public void visit(RepositoryAwareNotificationConfiguration config) {
        Map<String, NotificationConfigurationInterface> repositoryConfigs = config.getRepositories();

        if (repositoryConfigs != null) {
            for (Map.Entry<String, NotificationConfigurationInterface> entry : repositoryConfigs.entrySet()) {
                String subRepositoryFullName = entry.getKey();
                String org = subRepositoryFullName.replaceAll("^(.+)(/.+)", "$1");
                NotificationConfigurationInterface repositoryConfig = entry.getValue();

                Long repoInstallationId = installationRepository.getIdByOrg(org);

                if (repoInstallationId != null) {
                    Extending.MainConfig mainConfig = new Extending.MainConfig(context.getRepositoryFullName(), notification.getName());
                    Extending extending = new Extending(mainConfig, repositoryConfig);
                    try {
                        configGraphUpdater.updateEntry(extending, repoInstallationId, subRepositoryFullName, context.getTimestamp());
                    } catch (SchedulerException e) {
                        logger.error(e.getLocalizedMessage());
                    }
                }
            }
        }
    }

    public void visit(ScheduledNotificationConfigurationInterface config) {
        if (config.getSchedule().isPresent()) {
            try {
                configGraphUpdater.upsertSchedule(notification, context.getInstallationId(), context.getRepositoryFullName(), context.getTimestamp());
            } catch (SchedulerException e) {
                logger.error(e.getLocalizedMessage());
            }
        }
    }

    public void visit(SlackRealTimeUserNotificationConfiguration config) {
        eventMulticaster.addApplicationListener(new PrReviewRequestedListener());
    }
}