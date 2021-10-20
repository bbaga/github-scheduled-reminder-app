package com.bbaga.githubscheduledreminderapp.notifications;

public interface NotificationInterface<P> {
    void send(P data);
}
