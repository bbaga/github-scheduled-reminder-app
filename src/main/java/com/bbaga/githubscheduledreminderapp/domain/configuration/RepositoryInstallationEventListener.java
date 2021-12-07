package com.bbaga.githubscheduledreminderapp.domain.configuration;

import com.bbaga.githubscheduledreminderapp.application.jobs.GitHubInstallationScan;
import com.bbaga.githubscheduledreminderapp.domain.GitHubAppInstallationService;
import com.bbaga.githubscheduledreminderapp.infrastructure.configuration.InRepoConfig;
import com.bbaga.githubscheduledreminderapp.infrastructure.configuration.InRepoConfigParser;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.webhook.events.RepositoryInstallationEvent;
import com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling.InstallationScanJobScheduler;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

public class RepositoryInstallationEventListener implements ApplicationListener<RepositoryInstallationEvent> {

    private final ConfigGraphUpdater configGraphUpdater;
    private final InRepoConfigParser inRepoConfigParser;
    private final ObjectMapper objectMapper;
    private final GitHubAppInstallationService installationService;
    private final InstallationScanJobScheduler installationScanJobScheduler;
    private final Logger logger = LoggerFactory.getLogger(RepositoryInstallationEventListener.class);

    public RepositoryInstallationEventListener(
            ConfigGraphUpdater configGraphUpdater,
            InRepoConfigParser inRepoConfigParser,
            ObjectMapper objectMapper,
            GitHubAppInstallationService installationService,
            InstallationScanJobScheduler installationScanJobScheduler
    ) {
        this.configGraphUpdater = configGraphUpdater;
        this.inRepoConfigParser = inRepoConfigParser;
        this.objectMapper = objectMapper;
        this.installationService = installationService;
        this.installationScanJobScheduler = installationScanJobScheduler;
    }

    @Override
    public void onApplicationEvent(RepositoryInstallationEvent event) {
        System.out.println("Handling event - " + event.getClass().getName());
        Payload payload = null;

        try {
            payload = objectMapper.readValue(event.getBody(), Payload.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (payload == null) {
            return;
        }

        Installation installation = payload.getInstallation();
        GitHub gitHub = installationService.getClientByInstallationId(installation.getId());

        if (payload.getRepositorySelection().equals("selected")) {
            if (payload.getAction().equals("added")) {

                ArrayList<Repository> repositories = payload.getRepositoriesAdded();

                repositories.forEach((Repository repository) -> {
                    try {
                        GHRepository repo = gitHub.getRepository(repository.getFullName());
                        InRepoConfig inRepoConfig = inRepoConfigParser.getFrom(repo);

                        if (repo.isArchived() || !inRepoConfig.getEnabled()) {
                            return;
                        }

                        for (NotificationInterface notification : inRepoConfig.getNotifications()) {
                            configGraphUpdater.updateEntry(notification, installation.getId(), repo.getFullName(), Instant.now());
                        }
                    } catch (IOException e) {
                        logger.debug("No config file in {}", repository.getFullName());
                    } catch (SchedulerException e) {
                        logger.error(e.getMessage(), e);
                    }
                });
            } else if (payload.getAction().equals("removed")) {
                payload.getRepositoriesRemoved().forEach(
                    repository -> configGraphUpdater.removeRepository(installation.getId(), repository.getFullName())
                );
            }
        } else if (payload.getRepositorySelection().equals("all")) {
            try {
                installationScanJobScheduler.triggerJob(installation.getId());
            } catch (SchedulerException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Payload {
        private String action;
        private Installation installation;

        @JsonProperty("repository_selection")
        private String repositorySelection;

        @JsonProperty("repositories_added")
        private ArrayList<Repository> repositoriesAdded;

        @JsonProperty("repositories_removed")
        private ArrayList<Repository> repositoriesRemoved;

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public Installation getInstallation() {
            return installation;
        }

        public void setInstallation(Installation installation) {
            this.installation = installation;
        }

        public String getRepositorySelection() {
            return repositorySelection;
        }

        public void setRepositorySelection(String repositorySelection) {
            this.repositorySelection = repositorySelection;
        }

        public ArrayList<Repository> getRepositoriesAdded() {
            return repositoriesAdded;
        }

        public void setRepositoriesAdded(ArrayList<Repository> repositoriesAdded) {
            this.repositoriesAdded = repositoriesAdded;
        }

        public ArrayList<Repository> getRepositoriesRemoved() {
            return repositoriesRemoved;
        }

        public void setRepositoriesRemoved(ArrayList<Repository> repositoriesRemoved) {
            this.repositoriesRemoved = repositoriesRemoved;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Installation {
        private long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Repository {
        private long id;

        @JsonProperty("full_name")
        private String fullName;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
    }
}
