package com.bbaga.githubscheduledreminderapp.domain.configuration;

import com.bbaga.githubscheduledreminderapp.domain.configuration.configGraphUpdater.NotificationVisitor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification implements NotificationInterface {
    private String repository = "";
    private String name = "";
    private String schedule;
    private String type = "";

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type", defaultImpl = NotificationConfiguration.class)
    private Map<String, NotificationConfigurationInterface> repositories;

    @JsonSubTypes({
        @JsonSubTypes.Type(value = SlackNotificationConfiguration.class, name = "slack/channel"),
        @JsonSubTypes.Type(value = SlackNotificationConfiguration.class, name = "slack/scheduled/channel"),
    })
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type", defaultImpl = RepositoryAwareNotificationConfiguration.class)
    private NotificationConfigurationInterface config;

    @JsonProperty("timezone")
    private String timeZone;

    public Notification() {}

    public Notification(NotificationConfigurationInterface config) {
        this.setConfig(config);
    }

    public Notification(
            String name,
            String type,
            NotificationConfigurationInterface config
    ) {
        this.name = name;
        this.type= type;
        this.config = config;
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

    public String getFullName() {
        return getRepository() + "-" + getName();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public NotificationConfigurationInterface getConfig() {
        return config;
    }

    public void setConfig(NotificationConfigurationInterface config) {
        if (this.config == null && config instanceof SlackNotificationConfiguration) {
            SlackNotificationConfiguration scheduledConfig = (SlackNotificationConfiguration) config;

            if (repositories != null) {
                scheduledConfig.setRepositories(repositories);
                repositories = null;
            }

            if (schedule != null) {
                scheduledConfig.setSchedule(schedule);
                schedule = null;
            }

            if (timeZone != null) {
                scheduledConfig.setTimeZone(timeZone);
                timeZone = null;
            }
        }

        this.config = config;
    }

    @Override
    public void accept(NotificationVisitor visitor) {
        visitor.visit(this);
    }

    @Deprecated
    public String getTimeZone() {

        NotificationConfigurationInterface config = this.getConfig();

        if (config instanceof SlackNotificationConfiguration) {
            return ((SlackNotificationConfiguration) config).getTimeZone();
        }

        return timeZone;
    }

    @Deprecated
    public void setTimeZone(String timeZone) {
        NotificationConfigurationInterface config = this.getConfig();

        if (config instanceof SlackNotificationConfiguration) {
            ((SlackNotificationConfiguration) config).setTimeZone(timeZone);
        } else {
            this.timeZone = timeZone;
        }
    }

    @Deprecated
    public Map<String, NotificationConfigurationInterface> getRepositories() {
        NotificationConfigurationInterface config = this.getConfig();

        if (config instanceof SlackNotificationConfiguration) {
            return ((SlackNotificationConfiguration) config).getRepositories();
        }

        return this.repositories;
    }

    @Deprecated
    public void setRepositories(Map<String, NotificationConfigurationInterface> repositories) {
        NotificationConfigurationInterface config = this.getConfig();

        if (config instanceof SlackNotificationConfiguration) {
            ((SlackNotificationConfiguration) config).setRepositories(repositories);
        }
    }

    @Deprecated
    public Optional<String> getSchedule() {
        NotificationConfigurationInterface config = this.getConfig();

        if (config instanceof SlackNotificationConfiguration) {
            return ((SlackNotificationConfiguration) config).getSchedule();
        }

        return Optional.ofNullable(schedule);
    }

    @Deprecated
    public void setSchedule(String schedule) {
        NotificationConfigurationInterface config = this.getConfig();

        if (config instanceof SlackNotificationConfiguration) {
            ((SlackNotificationConfiguration) config).setSchedule(schedule);
        } else {
            this.schedule = schedule;
        }
    }
}
