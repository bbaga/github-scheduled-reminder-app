package com.bbaga.githubscheduledreminderapp.domain.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification {
    private String name;
    private String schedule;
    private String type;
    private Extending extending;
    private HashMap<String, ?> config = new HashMap<>();

    @JsonProperty("timezone")
    private String timeZone = "UTC";

    public Notification() {}

    public Notification(
            String name,
            String schedule,
            String type,
            HashMap<String, ?> config
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

    public String getSchedule() {
        return schedule;
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

    public HashMap<String, ?> getConfig() {
        return config;
    }

    public void setConfig(HashMap<String, String> config) {
        this.config = config;
    }

    public Extending getExtending() {
        return extending;
    }

    public void setExtending(Extending extending) {
        this.extending = extending;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
