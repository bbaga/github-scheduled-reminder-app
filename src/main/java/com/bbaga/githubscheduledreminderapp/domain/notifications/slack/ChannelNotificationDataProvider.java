package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.bbaga.githubscheduledreminderapp.domain.GitHubAppInstallationService;
import com.bbaga.githubscheduledreminderapp.domain.configuration.Notification;
import com.bbaga.githubscheduledreminderapp.domain.configuration.RepositoryRecord;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.SourceConfig;
import com.bbaga.githubscheduledreminderapp.domain.notifications.NotificationDataProviderInterface;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.RepositoryAsSourceInterface;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.SearchAsSourceInterface;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.SourceProvider;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import java.util.List;
import org.kohsuke.github.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ChannelNotificationDataProvider implements NotificationDataProviderInterface<ChannelNotificationDataProvider.Data> {

    private final Notification notification;
    private final ConcurrentHashMap<Integer, RepositoryRecord> repositories;
    private final GitHubAppInstallationService appInstallationService;

    private ChannelNotificationDataProvider(
        GitHubAppInstallationService appInstallationService,
        Notification notification,
        ConcurrentHashMap<Integer, RepositoryRecord> repositories
    ) {
        this.appInstallationService = appInstallationService;
        this.notification = notification;
        this.repositories = repositories;
    }

    public static ChannelNotificationDataProvider create(
        GitHubAppInstallationService appInstallationService,
        Notification notification,
        ConcurrentHashMap<Integer, RepositoryRecord> repositories
    ) {
        return new ChannelNotificationDataProvider(appInstallationService, notification, repositories);
    }

    public Data getData() {

        GitHub client;
        List<GitHubIssue> issues = new ArrayList<>();

        for (RepositoryRecord repository : repositories.values()) {
            client = appInstallationService.getClientByInstallationId(repository.getInstallationId());
            GHRepository repo;
            try {
                repo = client.getRepository(repository.getRepository());
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            if (repository.getConfig() != null
                && repository.getConfig().getSources() != null
                && repository.getConfig().getSources().size() > 0
            ) {
                for (SourceConfig sourceConfig : repository.getConfig().getSources()) {
                    issues.addAll(fetchDataFromSource(sourceConfig, repo, client));
                }
                // When the repository has its own config, skip the global config
                continue;
            }

            if (notification.getConfig() != null) {
                for (SourceConfig sourceConfig : notification.getConfig().getSources()) {
                    issues.addAll(fetchDataFromSource(sourceConfig, repo, client));
                }
            }
        }

        HashSet<String> uniqueIssues = new HashSet<>();
        issues = issues.stream().filter((GitHubIssue issue) -> {
            String id = issue.getRepository().getFullName() + "/" + issue.getNumber();
            if (!uniqueIssues.contains(id)) {
                uniqueIssues.add(id);
                return true;
            }

            return false;
        }).collect(Collectors.toCollection(ArrayList::new));
        uniqueIssues.clear();

        issues.sort((GitHubIssue issueA, GitHubIssue issueB) -> {
            try {
                return issueA.getCreatedAt().compareTo(issueB.getCreatedAt());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return 0;
        });

        return new Data(notification, issues);
    }

    private List<GitHubIssue> fetchDataFromSource(SourceConfig sourceConfig, GHRepository repo, GitHub client) {

        if (sourceConfig.hasSearchAsSource()) {
            SearchAsSourceInterface<GitHubIssue> source = SourceProvider.getSearchAsSourceProvider(sourceConfig);
            try {
                return source.get(repo, client);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (sourceConfig.hasRepositoryAsSource()) {
            RepositoryAsSourceInterface<GitHubIssue> source = SourceProvider.getRepositoryAsSourceProvider(sourceConfig);
            try {
                return source.get(repo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        throw new RuntimeException("Source config has unknown source type");
    }

    public static class Data {
        private final Notification notification;
        private final List<GitHubIssue> issues;

        public Data(Notification notification, List<GitHubIssue> issues) {
            this.notification = notification;
            this.issues = issues;
        }

        public Notification getNotification() {
            return notification;
        }

        public List<GitHubIssue> getIssues() {
            return issues;
        }
    }
}
