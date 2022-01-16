package com.bbaga.githubscheduledreminderapp.domain.configuration;

import com.bbaga.githubscheduledreminderapp.domain.configuration.configGraphUpdater.NotificationVisitor;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

public class Extending implements NotificationInterface {
    private MainConfig extending;

    @JsonSubTypes({
        @JsonSubTypes.Type(value = NotificationConfiguration.class),
    })
    @JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, defaultImpl = NotificationConfiguration.class)
    private NotificationConfigurationInterface config;

    public Extending() {}

    public Extending(MainConfig mainConfig, NotificationConfigurationInterface config) {
        this.setExtending(mainConfig);
        this.setConfig(config);
    }

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

        public MainConfig(String repository, String name) {
            this.setRepository(repository);
            this.setName(name);
        }

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

    @Override
    public void accept(NotificationVisitor visitor) {
        visitor.visit(this);
    }
}
