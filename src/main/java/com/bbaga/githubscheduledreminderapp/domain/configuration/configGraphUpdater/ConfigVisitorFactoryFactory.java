package com.bbaga.githubscheduledreminderapp.domain.configuration.configGraphUpdater;

import com.bbaga.githubscheduledreminderapp.infrastructure.github.repositories.GitHubInstallationRepository;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.webhook.EventPublisher;
import org.springframework.context.event.ApplicationEventMulticaster;

public class ConfigVisitorFactoryFactory {
    private final GitHubInstallationRepository installationRepository;
    private final ApplicationEventMulticaster eventMulticaster;

    public ConfigVisitorFactoryFactory(
        GitHubInstallationRepository installationRepository,
        ApplicationEventMulticaster eventMulticaster
    ) {
        this.installationRepository = installationRepository;
        this.eventMulticaster = eventMulticaster;
    }

    public ConfigVisitorFactory create(ConfigGraphUpdater configGraphUpdater) {
        return new ConfigVisitorFactory(configGraphUpdater, installationRepository, eventMulticaster);
    }
}
