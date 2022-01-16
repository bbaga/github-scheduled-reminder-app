package com.bbaga.githubscheduledreminderapp.domain.configuration.configGraphUpdater;

import com.bbaga.githubscheduledreminderapp.infrastructure.github.repositories.GitHubInstallationRepository;
import org.springframework.context.event.ApplicationEventMulticaster;

public class ConfigVisitorFactoryFactory {
    private final GitHubInstallationRepository installationRepository;

    public ConfigVisitorFactoryFactory(
        GitHubInstallationRepository installationRepository
    ) {
        this.installationRepository = installationRepository;
    }

    public ConfigVisitorFactory create(ConfigGraphUpdater configGraphUpdater) {
        return new ConfigVisitorFactory(configGraphUpdater, installationRepository);
    }
}
