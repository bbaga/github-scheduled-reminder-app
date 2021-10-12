package com.bbaga.githubscheduledreminderapp.configuration;

import java.util.HashMap;

public class Notification {
    private String name;
    private String schedule;
    private String type;
    private Extending extending;
    private HashMap<String, ?> config = new HashMap<>();

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
}
