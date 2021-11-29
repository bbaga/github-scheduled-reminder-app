package com.bbaga.githubscheduledreminderapp.domain.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification implements NotificationInterface {
    private String name = "";
    private String schedule;
    private String type = "";

    @JsonSubTypes({
        @JsonSubTypes.Type(value = SlackNotification.class, name = "slack/channel"),
    })
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type", defaultImpl = NotificationConfiguration.class)
    private NotificationConfigurationInterface config;

    @JsonProperty("timezone")
    private String timeZone = "UTC";

    public Notification() {}

    public Notification(
            String name,
            String schedule,
            String type,
            NotificationConfigurationInterface config
    ) {
        this.name = name;
        this.schedule = schedule;
        this.type= type;
        this.config = config;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Optional<String> getSchedule() {
        return Optional.ofNullable(schedule);
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

//    @Override
    public NotificationConfigurationInterface getConfig() {
        return config;
    }

//    @Override
    public void setConfig(NotificationConfigurationInterface config) {
        this.config = config;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
