package com.bbaga.githubscheduledreminderapp.domain.configuration.configGraphUpdater;

import com.bbaga.githubscheduledreminderapp.domain.configuration.Notification;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.repositories.GitHubInstallationRepository;
import org.springframework.context.event.ApplicationEventMulticaster;

public class ConfigVisitorFactory {
    private final ConfigGraphUpdater configGraphUpdater;
    private final GitHubInstallationRepository installationRepository;
    private final ApplicationEventMulticaster eventMulticaster;

    public ConfigVisitorFactory(
        ConfigGraphUpdater configGraphUpdater,
        GitHubInstallationRepository installationRepository,
        ApplicationEventMulticaster eventMulticaster
    ) {
        this.configGraphUpdater = configGraphUpdater;
        this.installationRepository = installationRepository;
        this.eventMulticaster = eventMulticaster;
    }

    public NotificationConfigVisitor create(Notification notification, EntryContext context) {
        return new NotificationConfigVisitor(
            configGraphUpdater,
            installationRepository,
            notification,
            eventMulticaster,
            context
        );
    }
}
