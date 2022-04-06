package com.bbaga.githubscheduledreminderapp.application.config;

import com.bbaga.githubscheduledreminderapp.domain.GitHubAppInstallationService;
import com.bbaga.githubscheduledreminderapp.domain.configuration.ConfigGraphNode;
import com.bbaga.githubscheduledreminderapp.domain.configuration.configGraphUpdater.ConfigGraphUpdater;
import com.bbaga.githubscheduledreminderapp.domain.configuration.configGraphUpdater.ConfigVisitorFactoryFactory;
import com.bbaga.githubscheduledreminderapp.domain.notifications.slack.ChannelMessageBuilder;
import com.bbaga.githubscheduledreminderapp.domain.statistics.UrlBuilderInterface;
import com.bbaga.githubscheduledreminderapp.infrastructure.configuration.InRepoConfigParser;
import com.bbaga.githubscheduledreminderapp.domain.configuration.RepositoryInstallationEventListener;
import com.bbaga.githubscheduledreminderapp.infrastructure.configuration.persitance.ConfigPersistenceInterface;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.webhook.EventPublisher;
import com.bbaga.githubscheduledreminderapp.application.jobs.GitHubInstallationScan;
import com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling.InstallationScanJobScheduler;
import com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling.NotificationJobScheduler;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.repositories.GitHubInstallationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
@ComponentScan("com.bbaga.githubscheduledreminderapp")
public class Config {
    @Value("${application.github.configFilePath}")
    private String configFilePath;

    @Value("${application.jobs.github.installationScan.interval}")
    private String installationScanIntervals;

    @Bean
    @Qualifier("SimpleTriggerFactoryBean")
    public JobDetailFactoryBean jobDetail() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(GitHubInstallationScan.class);
        jobDetailFactory.setDescription("Invoke Sample Job service...");
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }

    @Bean
    @Qualifier("SimpleTriggerFactoryBean")
    public SimpleTriggerFactoryBean trigger(JobDetail jobDetail) {
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(jobDetail);
        trigger.setRepeatInterval(Long.parseLong(installationScanIntervals));
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        return trigger;
    }

    @Bean
    public GitHubInstallationRepository gitHubInstallationRepository() {
        return new GitHubInstallationRepository();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    @Qualifier("ConfigGraph")
    public ConcurrentHashMap<String, ConfigGraphNode> initializeConfigGraph(ConfigPersistenceInterface persistentConfigStorage) {
        return persistentConfigStorage.load();
    }

    @Bean
    @Qualifier("application.configFilePath")
    public String getConfigFilePath() {
        return this.configFilePath;
    }

    @Bean
    public ConfigGraphUpdater getConfigGraphUpdater(
        @Qualifier("ConfigGraph") ConcurrentHashMap<String, ConfigGraphNode> configGraph,
        NotificationJobScheduler notificationJobScheduler,
        GitHubInstallationRepository installationRepository
    ) {
        ConfigVisitorFactoryFactory visitorFactoryFactory = new ConfigVisitorFactoryFactory(installationRepository);
        return new ConfigGraphUpdater(visitorFactoryFactory, configGraph, notificationJobScheduler);
    }

    @Bean
    public InRepoConfigParser getInRepoConfigParser(
        @Qualifier("YamlObjectMapper") ObjectMapper mapper,
        @Qualifier("application.configFilePath") String configFilePath
    ) {
        return new InRepoConfigParser(mapper, configFilePath);
    }

    @Bean
    public EventPublisher getEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        EventPublisher eventPublisher = new EventPublisher();
        eventPublisher.setApplicationEventPublisher(applicationEventPublisher);

        return eventPublisher;
    }

    @Bean
    @Qualifier("YamlObjectMapper")
    public ObjectMapper getYamlObjectMapper() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        return mapper;
    }

    @Bean
    @Primary
    @Qualifier("JsonObjectMapper")
    public ObjectMapper getJsonObjectMapper() {
        return JsonMapper.builder().findAndAddModules().build();
    }

    @Bean
    public RepositoryInstallationEventListener getGitHubInstallationEventListener(
        ConfigGraphUpdater configGraphUpdater,
        InRepoConfigParser inRepoConfigParser,
        @Qualifier("JsonObjectMapper") ObjectMapper objectMapper,
        GitHubAppInstallationService installationService,
        InstallationScanJobScheduler installationScanJobScheduler
    ) {
        return new RepositoryInstallationEventListener(
            configGraphUpdater,
            inRepoConfigParser,
            objectMapper,
            installationService,
            installationScanJobScheduler
        );
    }

    @Bean
    public ChannelMessageBuilder getChannelMessageBuilder(UrlBuilderInterface urlBuilder) {
        return new ChannelMessageBuilder(urlBuilder);
    }
}
