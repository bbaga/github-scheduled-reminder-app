package com.bbaga.githubscheduledreminderapp.domain.configuration;

public class SlackNotification extends NotificationConfiguration {
    private String channel;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
