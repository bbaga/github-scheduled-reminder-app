package com.bbaga.githubscheduledreminderapp.infrastructure.configuration;

import com.bbaga.githubscheduledreminderapp.domain.configuration.Notification;

import java.util.ArrayList;
import java.util.List;

public class InRepoConfig {
    private Boolean enabled;
    private List<Notification> notifications = new ArrayList<>();

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}
