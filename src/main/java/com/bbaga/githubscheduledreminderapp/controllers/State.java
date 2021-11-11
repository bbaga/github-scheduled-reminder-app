package com.bbaga.githubscheduledreminderapp.controllers;

import com.bbaga.githubscheduledreminderapp.configuration.*;
import org.kohsuke.github.*;
import org.quartz.SchedulerException;
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
    private final InRepoConfigParser inRepoConfigParser;

    State(
        GitHub gitHubClient,
        ConcurrentHashMap<String, ConfigGraphNode> state,
        ConfigGraphUpdater configGraphUpdater,
        InRepoConfigParser inRepoConfigParser
    ) {
        this.gitHubClient = gitHubClient;
        this.state = state;
        this.configGraphUpdater = configGraphUpdater;
        this.inRepoConfigParser = inRepoConfigParser;
    }

    @GetMapping("/state/config")
    public ConcurrentHashMap<String, ConfigGraphNode> config() {
        return this.state;
    }

    @GetMapping("/state/update/repo/{org}/{repository}")
    public String updateRepo(@PathVariable String org, @PathVariable String repository) throws IOException {
        String repositoryFullName = String.format("%s/%s", org, repository);
        GHAppInstallation installation = gitHubClient.getApp().getInstallationByRepository(org, repository);
        GHAppInstallationToken token = installation.createToken().create();
        long installationId = installation.getId();
        Instant timestamp = Instant.now();

        GitHub installationClient = new GitHubBuilder()
                .withAppInstallationToken(token.getToken())
                .build();

        GHRepository repo = installationClient.getRepository(String.format("%s/%s", org, repository));

        try {
            InRepoConfig inRepoConfig = inRepoConfigParser.getFrom(repo);

            if (repo.isArchived() || !inRepoConfig.getEnabled()) {
                return "Repository is archived or the config is disabled";
            }

            for (Notification notification : inRepoConfig.getNotifications()) {
                configGraphUpdater.updateEntry(notification, installationId, repositoryFullName, timestamp);
            }

            configGraphUpdater.clearOutdated(installationId, repositoryFullName, timestamp);
        } catch (GHFileNotFoundException | SchedulerException e) {
            return e.getMessage();
        }

        return "OK";
    }
}
