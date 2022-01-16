package com.bbaga.githubscheduledreminderapp.domain.configuration.configGraphUpdater;

import com.bbaga.githubscheduledreminderapp.domain.configuration.Notification;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.repositories.GitHubInstallationRepository;

public class ConfigVisitorFactory {
    private final ConfigGraphUpdater configGraphUpdater;
    private final GitHubInstallationRepository installationRepository;

    public ConfigVisitorFactory(
        ConfigGraphUpdater configGraphUpdater,
        GitHubInstallationRepository installationRepository
    ) {
        this.configGraphUpdater = configGraphUpdater;
        this.installationRepository = installationRepository;
    }

    public NotificationConfigVisitor create(Notification notification, EntryContext context) {
        return new NotificationConfigVisitor(
            configGraphUpdater,
            installationRepository,
            notification,
            context
        );
    }
}
