package com.bbaga.githubscheduledreminderapp.infrastructure.github.webhook.events;

public class RepositoryInstallationEvent extends WebhookEvent {
    public RepositoryInstallationEvent(Object source, String body) {
        super(source, body);
    }

    public static RepositoryInstallationEvent create(Object source, String body) {
        return new RepositoryInstallationEvent(source, body);
    }
}
