package com.bbaga.githubscheduledreminderapp.application.controllers;

import com.bbaga.githubscheduledreminderapp.domain.configuration.*;
import com.bbaga.githubscheduledreminderapp.infrastructure.configuration.InRepoConfig;
import com.bbaga.githubscheduledreminderapp.infrastructure.configuration.InRepoConfigParser;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubBuilderFactory;
import org.kohsuke.github.*;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class State {

    private final ConcurrentHashMap<String, ConfigGraphNode> state;
    private final ConfigGraphUpdater configGraphUpdater;
    private final GitHub gitHubClient;
    private final GitHubBuilderFactory gitHubBuilderFactory;
    private final InRepoConfigParser inRepoConfigParser;
    private final Logger logger = LoggerFactory.getLogger(State.class);

    State(
        GitHub gitHubClient,
        GitHubBuilderFactory gitHubBuilderFactory,
        ConcurrentHashMap<String, ConfigGraphNode> state,
        ConfigGraphUpdater configGraphUpdater,
        InRepoConfigParser inRepoConfigParser
    ) {
        this.gitHubClient = gitHubClient;
        this.gitHubBuilderFactory = gitHubBuilderFactory;
        this.state = state;
        this.configGraphUpdater = configGraphUpdater;
        this.inRepoConfigParser = inRepoConfigParser;
    }

    @GetMapping("/state/config")
    public ConcurrentHashMap<String, ConfigGraphNode> config() {
        return this.state;
    }

    @GetMapping("/state/config/buffer")
    public ConcurrentHashMap<String, ConcurrentHashMap<Integer, RepositoryRecord>> configBuffer() {
        return configGraphUpdater.getBuffer();
    }

    @GetMapping("/state/update/repo/{org}/{repository}")
    public String updateRepo(@PathVariable String org, @PathVariable String repository) throws IOException {
        String repositoryFullName = String.format("%s/%s", org, repository);
        GHAppInstallation installation = gitHubClient.getApp().getInstallationByRepository(org, repository);
        GHAppInstallationToken token = installation.createToken().create();
        long installationId = installation.getId();
        Instant timestamp = Instant.now();

        GitHub installationClient = gitHubBuilderFactory.create()
                .withAppInstallationToken(token.getToken())
                .build();

        GHRepository repo = installationClient.getRepository(String.format("%s/%s", org, repository));

        try {
            InRepoConfig inRepoConfig = inRepoConfigParser.getFrom(repo);

            if (repo.isArchived() || !inRepoConfig.getEnabled()) {
                return "Repository is archived or the config is disabled";
            }

            for (NotificationInterface notification : inRepoConfig.getNotifications()) {
                configGraphUpdater.updateEntry(notification, installationId, repositoryFullName, timestamp);
            }

            configGraphUpdater.clearOutdated(installationId, repositoryFullName, timestamp);
        } catch (GHFileNotFoundException | SchedulerException e) {
            logger.error(e.getMessage());
            return "Something wen wrong, please check the logs.";
        }

        return "OK";
    }
}
