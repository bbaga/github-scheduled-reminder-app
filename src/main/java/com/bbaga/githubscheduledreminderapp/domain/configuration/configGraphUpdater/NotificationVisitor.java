package com.bbaga.githubscheduledreminderapp.domain.configuration.configGraphUpdater;

import com.bbaga.githubscheduledreminderapp.domain.configuration.Extending;
import com.bbaga.githubscheduledreminderapp.domain.configuration.Notification;
import com.bbaga.githubscheduledreminderapp.domain.configuration.NotificationConfigurationInterface;

public class NotificationVisitor {
    private final ConfigGraphUpdater configGraphUpdater;
    private final EntryContext context;
    private final ConfigVisitorFactory configVisitorFactory;

    public NotificationVisitor(ConfigGraphUpdater configGraphUpdater, ConfigVisitorFactory configVisitorFactory, EntryContext context) {
        this.configGraphUpdater = configGraphUpdater;
        this.context = context;
        this.configVisitorFactory = configVisitorFactory;
    }

    public void visit(Notification notification) {
        NotificationConfigurationInterface config = notification.getConfig();
        config.accept(this.configVisitorFactory.create(notification, context));
    }

    public void visit(Extending notification) {
        configGraphUpdater.updateRepoEntry(notification, context.getInstallationId(), context.getRepositoryFullName(), context.getTimestamp());
    }
}
