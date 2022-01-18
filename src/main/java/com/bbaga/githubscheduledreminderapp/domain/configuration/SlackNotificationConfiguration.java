package com.bbaga.githubscheduledreminderapp.domain.configuration;

import com.bbaga.githubscheduledreminderapp.domain.configuration.configGraphUpdater.NotificationConfigVisitor;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.RepositoryIssuesSourceConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.RepositoryPRsSourceConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.SourceConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.DraftFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.LabelFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.template.TemplateConfig;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Optional;

import static com.bbaga.githubscheduledreminderapp.domain.configuration.sources.Sources.Constants.REPOSITORY_ISSUES;
import static com.bbaga.githubscheduledreminderapp.domain.configuration.sources.Sources.Constants.REPOSITORY_PRS;

public class SlackNotificationConfiguration extends RepositoryAwareNotificationConfiguration implements ScheduledNotificationConfigurationInterface {
    private String channel;
    private String schedule;

    @JsonProperty("template-config")
    private TemplateConfig templateConfig;

    @JsonProperty("timezone")
    private String timeZone = "UTC";

    public SlackNotificationConfiguration() {}

    public SlackNotificationConfiguration(String schedule) {
        super();
        this.setSchedule(schedule);
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Override
    public Optional<String> getSchedule() {
        return Optional.ofNullable(schedule);
    }

    @Override
    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    @Override
    public String getTimeZone() {
        return timeZone;
    }

    @Override
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public ArrayList<SourceConfig> getSources() {
        return super.getSources().size() == 0 ? getDefaultSources() : super.getSources();
    }

    public TemplateConfig getTemplateConfig() {
        return templateConfig;
    }

    public void setTemplateConfig(TemplateConfig templateConfig) {
        this.templateConfig = templateConfig;
    }

    @Override
    public void accept(NotificationConfigVisitor configVisitor) {
        super.accept(configVisitor);
        configVisitor.visit((ScheduledNotificationConfigurationInterface) this);
    }

    private ArrayList<SourceConfig> getDefaultSources() {
        ArrayList<SourceConfig> sourceConfigs = new ArrayList<>();
        ArrayList<AbstractFilterConfig> prFilters = new ArrayList<>();
        prFilters.add(new DraftFilterConfig());

        LabelFilterConfig labelFilterConfig = new LabelFilterConfig();

        ArrayList<AbstractFilterConfig> issueFilters = new ArrayList<>();
        issueFilters.add(labelFilterConfig);

        sourceConfigs.add(new RepositoryIssuesSourceConfig(REPOSITORY_ISSUES, issueFilters));
        sourceConfigs.add(new RepositoryPRsSourceConfig(REPOSITORY_PRS, prFilters));

        return sourceConfigs;
    }
}
