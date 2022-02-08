package com.bbaga.githubscheduledreminderapp.infrastructure.configuration;

import com.bbaga.githubscheduledreminderapp.domain.configuration.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InRepoConfig {
    private Boolean enabled;
    private List<NotificationInterface> notifications = new ArrayList<>();

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @JsonSubTypes({
        @JsonSubTypes.Type(value = Notification.class),
        @JsonSubTypes.Type(value = Extending.class),
    })
    @JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    public List<NotificationInterface> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationInterface> notifications) {
        this.notifications = notifications;
    }
}
