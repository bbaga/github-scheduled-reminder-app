package com.bbaga.githubscheduledreminderapp;

import com.bbaga.githubscheduledreminderapp.configuration.ConfigGraphNode;
import com.bbaga.githubscheduledreminderapp.jobs.GitHubInstallationScan;
import com.bbaga.githubscheduledreminderapp.repositories.GitHubInstallationRepository;
import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.SlackClientFactory;
import com.hubspot.slack.client.SlackClientRuntimeConfig;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.extras.authorization.JWTTokenProvider;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class Config {
    @Value("${application.gitHubAppCert}")
    private String gitHubAppCert;

    @Value("${application.github.id}")
    private String githubApplicationId;

    @Value("${application.slack.apiToken}")
    private String slackApiToken;

    @Value("${application.jobs.github.installationScan.interval}")
    private String installationScanIntervals;

    @Bean
    public GitHub getGitHubClient() throws Exception {
        String privateKeyPEM = gitHubAppCert
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll(" ", "");

        return new GitHubBuilder().withAuthorizationProvider(new JWTTokenProvider(githubApplicationId, privateKeyPEM)).build();
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
    public GitHubInstallationRepository gitHubInstallationRepository() {
        return new GitHubInstallationRepository();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public SlackClient slackApp() {
        SlackClientRuntimeConfig runtimeConfig = SlackClientRuntimeConfig.builder()
                .setTokenSupplier(() -> slackApiToken)
                .build();

        return SlackClientFactory.defaultFactory().build(runtimeConfig);
    }

    @Bean
    @Qualifier("ConfigGraph")
    public ConcurrentHashMap<String, ConfigGraphNode> initializeConfigGraph() {
        return new ConcurrentHashMap<String, ConfigGraphNode>();
    }
}
