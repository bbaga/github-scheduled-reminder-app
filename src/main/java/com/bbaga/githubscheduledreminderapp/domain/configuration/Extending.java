package com.bbaga.githubscheduledreminderapp.domain.configuration;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

public class Extending implements NotificationInterface {
    private MainConfig extending;

    @JsonSubTypes({
            @JsonSubTypes.Type(value = SlackNotification.class),
            @JsonSubTypes.Type(value = NotificationConfiguration.class),
    })
    @JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, defaultImpl = NotificationConfiguration.class)
    private NotificationConfigurationInterface config;

    public Extending() {}

    public MainConfig getExtending() {
        return extending;
    }

    public void setExtending(MainConfig extending) {
        this.extending = extending;
    }

    @Override
    public NotificationConfigurationInterface getConfig() {
        return this.config;
    }

    @Override
    public void setConfig(NotificationConfigurationInterface config) {
        this.config = config;
    }

    public static class MainConfig {
        private String repository = "";
        private String name = "";

        public MainConfig() {}

        public String getRepository() {
            return repository;
        }

        public void setRepository(String repository) {
            this.repository = repository;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
