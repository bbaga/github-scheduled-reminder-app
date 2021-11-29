package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.bbaga.githubscheduledreminderapp.domain.GitHubAppInstallationService;
import com.bbaga.githubscheduledreminderapp.domain.configuration.Notification;
import com.bbaga.githubscheduledreminderapp.domain.configuration.RepositoryRecord;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.Source;
import com.bbaga.githubscheduledreminderapp.domain.notifications.NotificationDataProviderInterface;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.RepositoryAsSourceInterface;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.SearchAsSourceInterface;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.SourceProvider;
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
        ArrayList<GHIssue> issues = new ArrayList<>();
        ArrayList<Source> repoAsSource = new ArrayList<>();

        for (Source sourceConfig : notification.getConfig().getSources()) {
            if (sourceConfig.isRepositoryAsSource()) {
                repoAsSource.add(sourceConfig);
            }
        }

        for (RepositoryRecord repository : repositories.values()) {
            client = appInstallationService.getClientByInstallationId(repository.getInstallationId());
            GHRepository repo;
            try {
                repo = client.getRepository(repository.getRepository());
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            if (repository.getConfig() != null && repository.getConfig().getSources().size() > 0) {
                for (Source sourceConfig : repository.getConfig().getSources()) {
                    if (sourceConfig.isSearchAsSource()) {
                        SearchAsSourceInterface<GHIssue> source = SourceProvider.getSearchAsSourceProvider(sourceConfig);
                        try {
                            issues.addAll(source.get(repo, client));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                continue;
            }

            for (Source sourceConfig : repoAsSource) {
                RepositoryAsSourceInterface<GHIssue> source = SourceProvider.getRepositoryAsSourceProvider(sourceConfig);
                try {
                    issues.addAll(source.get(repo));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        HashSet<String> uniqueIssues = new HashSet<>();
        issues = issues.stream().filter((GHIssue issue) -> {
            String id = issue.getRepository().getFullName() + "/" + issue.getNumber();
            if (!uniqueIssues.contains(id)) {
                uniqueIssues.add(id);
                return true;
            }

            return false;
        }).collect(Collectors.toCollection(ArrayList::new));
        uniqueIssues.clear();

        issues.sort((GHIssue issueA, GHIssue issueB) -> {
            try {
                return issueA.getCreatedAt().compareTo(issueB.getCreatedAt());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return 0;
        });

        return new Data(notification, issues);
    }

    public static class Data {
        private final Notification notification;
        private final ArrayList<GHIssue> issues;

        public Data(Notification notification, ArrayList<GHIssue> issues) {
            this.notification = notification;
            this.issues = issues;
        }

        public Notification getNotification() {
            return notification;
        }

        public ArrayList<GHIssue> getIssues() {
            return issues;
        }
    }
}
