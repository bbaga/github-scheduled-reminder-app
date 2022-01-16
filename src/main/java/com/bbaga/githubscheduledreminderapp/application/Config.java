package com.bbaga.githubscheduledreminderapp.application;

import com.bbaga.githubscheduledreminderapp.domain.GitHubAppInstallationService;
import com.bbaga.githubscheduledreminderapp.domain.configuration.ConfigGraphNode;
import com.bbaga.githubscheduledreminderapp.domain.configuration.configGraphUpdater.ConfigGraphUpdater;
import com.bbaga.githubscheduledreminderapp.domain.configuration.configGraphUpdater.ConfigVisitorFactoryFactory;
import com.bbaga.githubscheduledreminderapp.infrastructure.configuration.InRepoConfigParser;
import com.bbaga.githubscheduledreminderapp.domain.configuration.RepositoryInstallationEventListener;
import com.bbaga.githubscheduledreminderapp.infrastructure.configuration.persitance.ConfigPersistenceInterface;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubBuilderFactory;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.webhook.EventPublisher;
import com.bbaga.githubscheduledreminderapp.application.jobs.GitHubInstallationScan;
import com.bbaga.githubscheduledreminderapp.application.jobs.ScheduledStateDump;
import com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling.InstallationScanJobScheduler;
import com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling.NotificationJobScheduler;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.repositories.GitHubInstallationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.SlackClientFactory;
import com.hubspot.slack.client.SlackClientRuntimeConfig;
import org.apache.commons.io.IOUtils;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.extras.authorization.JWTTokenProvider;
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

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@ComponentScan("com.bbaga.githubscheduledreminderapp")
public class Config {
    @Value("${application.configFilePath}")
    private String configFilePath;

    @Value("${application.gitHubEndpoint}")
    private String gitHubEndpoint;

    @Value("${application.gitHubAppCert}")
    private String gitHubAppCert;

    @Value("${application.gitHubAppCertFile}")
    private String gitHubAppCertFile = "";

    @Value("${application.github.id}")
    private String githubApplicationId;

    @Value("${application.slack.apiToken}")
    private String slackApiToken;

    @Value("${application.slack.apiTokenFile}")
    private String slackApiTokenFile;

    @Value("${application.jobs.github.installationScan.interval}")
    private String installationScanIntervals;

    @Bean
    public GitHub getGitHubClient(GitHubBuilderFactory gitHubBuilderFactory) throws Exception {
        if (!gitHubAppCertFile.isEmpty() && gitHubAppCert.isEmpty()) {
            try(FileInputStream inputStream = new FileInputStream(gitHubAppCertFile)) {
                gitHubAppCert = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            }
        }

        if (gitHubAppCert.isEmpty()) {
            throw new IllegalStateException("GitHub certificate must be configured, see the GITHUB_APP_CERT and GITHUB_APP_CERT_FILE environment variables.");
        }

        String privateKeyPEM = gitHubAppCert
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll(" ", "");

        return gitHubBuilderFactory.create().withAuthorizationProvider(new JWTTokenProvider(githubApplicationId, privateKeyPEM)).build();
    }

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
    public SimpleTriggerFactoryBean trigger(JobDetail jobDetail) throws Exception {
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(jobDetail);
        trigger.setRepeatInterval(Long.parseLong(installationScanIntervals));
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        return trigger;
    }

    @Bean
    @Qualifier("Scheduler")
    public Scheduler schedulerConfig(Scheduler scheduler) throws Exception {

        JobDetail job = JobBuilder.newJob(ScheduledStateDump.class)
                .withIdentity(ScheduledStateDump.class.getName())
                .storeDurably(true)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(ScheduledStateDump.class.getName())
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(1).repeatForever())
                .build();

        scheduler.scheduleJob(job, trigger);

        return scheduler;
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
    public SlackClient slackApp() throws Exception {
        if (!slackApiTokenFile.isEmpty() && slackApiToken.isEmpty()) {
            try(FileInputStream inputStream = new FileInputStream(slackApiTokenFile)) {
                slackApiToken = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            }
        }

        if (slackApiToken.isEmpty()) {
            throw new IllegalStateException("Slack API Token must be configured, see the SLACK_API_TOKEN and SLACK_API_TOKEN_FILE environment variables.");
        }

        SlackClientRuntimeConfig runtimeConfig = SlackClientRuntimeConfig.builder()
            .setTokenSupplier(() -> slackApiToken)
            .build();

        return SlackClientFactory.defaultFactory().build(runtimeConfig);
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
    @Qualifier("application.gitHubEndpoint")
    public String getGitHubEndpoint() {
        return this.gitHubEndpoint;
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
    public GitHubBuilderFactory getGitHubBuilderFactory(@Qualifier("application.gitHubEndpoint") String gitHubEndpoint) {
        return new GitHubBuilderFactory(gitHubEndpoint);
    }

    @Bean
    public NotificationJobScheduler getNotificationJobScheduler(@Qualifier("Scheduler") Scheduler scheduler) {
        return new NotificationJobScheduler(scheduler);
    }

    @Bean
    public InstallationScanJobScheduler getInstallationScanJobScheduler(@Qualifier("Scheduler") Scheduler scheduler) {
        return new InstallationScanJobScheduler(scheduler);
    }

    @Bean
    public EventPublisher getGitHubEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
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
}
