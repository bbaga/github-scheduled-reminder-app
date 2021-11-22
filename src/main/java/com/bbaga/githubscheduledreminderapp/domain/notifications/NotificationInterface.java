package com.bbaga.githubscheduledreminderapp.domain.notifications;

public interface NotificationInterface<P> {
    void send(P data);
}
