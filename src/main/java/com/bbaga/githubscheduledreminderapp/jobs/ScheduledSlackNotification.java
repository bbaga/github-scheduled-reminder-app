package com.bbaga.githubscheduledreminderapp.jobs;

import com.bbaga.githubscheduledreminderapp.configuration.ConfigGraphNode;
import com.bbaga.githubscheduledreminderapp.configuration.Notification;
import com.bbaga.githubscheduledreminderapp.configuration.configgraphnode.RepositoryRecord;
import com.bbaga.githubscheduledreminderapp.repositories.GitHubInstallationRepository;
import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.methods.params.chat.ChatPostMessageParams;
import com.hubspot.slack.client.models.blocks.Block;
import com.hubspot.slack.client.models.blocks.Divider;
import com.hubspot.slack.client.models.blocks.Header;
import com.hubspot.slack.client.models.blocks.Section;
import com.hubspot.slack.client.models.blocks.elements.Button;
import com.hubspot.slack.client.models.blocks.objects.Text;
import com.hubspot.slack.client.models.blocks.objects.TextType;
import org.kohsuke.github.*;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ScheduledSlackNotification implements Job {

    @Autowired
    private GitHubInstallationRepository installationRepository;

    @Autowired
    private SlackClient slackClient;

    @Autowired
    @Qualifier("ConfigGraph")
    private ConcurrentHashMap<String, ConfigGraphNode> configGraph;

    private final Logger logger = LoggerFactory.getLogger(ScheduledSlackNotification.class);
    private final HashMap<Long, GitHub> installationClients = new HashMap<>();

    public void execute(JobExecutionContext context) throws JobExecutionException {
        String jobName = context.getJobDetail().getKey().getName();
        logger.info("Starting notification job for {}", jobName);

        Notification notification = configGraph.get(jobName).getNotification();
        ConcurrentHashMap<Integer, RepositoryRecord> repositories = configGraph.get(jobName).getRepositories();

        GitHub client;
        Set<GHIssue> issues = new HashSet<>();
        Set<GHIssue> pullRequests = new HashSet<>();
        for (RepositoryRecord repository : repositories.values()) {
            client = grabInstallationClient(repository.getInstallationId());
            try {
                client.getRepository(repository.getRepository()).getIssues(GHIssueState.OPEN).forEach((GHIssue issue) -> {
                    if (issue.isPullRequest()) {
                        pullRequests.add(issue);
                    } else {
                        issues.add(issue);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        sendSlackMessage(notification,issues, pullRequests);

        logger.info("Finished notification job for {}", jobName);
    }

    private void sendSlackMessage(Notification notification, Set<GHIssue> issues, Set<GHIssue> pullRequests) {
        List<Block> sections = new ArrayList<>();
        sections.add(Header.of(Text.of(TextType.PLAIN_TEXT, "Reporting open issues and pull requests")));

        if ((issues.size() + pullRequests.size()) == 0) {
            sections.add(Section.of(Text.of(TextType.MARKDOWN, "*There aren't any open issues or pull requests.*")));
        }

        if (issues.size() > 0) {
            sections.add(Section.of(Text.of(TextType.MARKDOWN, "*Open Issues:*")));
            sections.add(Divider.builder().build());
        }

        String user;
        for (GHIssue issue : issues) {
            try {
                user = issue.getUser().getLogin();
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage());
                continue;
            }

            sections.add(
                Section.of(
                    Text.of(
                        TextType.MARKDOWN,
                        String.format(
                            "%s *%s*%nrepository: %s",
                            user,
                            issue.getTitle(),
                            issue.getRepository().getFullName()
                        )
                    )
                ).withAccessory(
                    Button.of(
                        Text.of(TextType.PLAIN_TEXT, "Open"),
                        issue.getNodeId()
                    ).withUrl(issue.getHtmlUrl().toString())));
        }

        if (pullRequests.size() > 0) {
            sections.add(Section.of(Text.of(TextType.MARKDOWN, "*Open Pull Requests:*")));
            sections.add(Divider.builder().build());
        }

        GHRepository repository;
        GHPullRequest pullRequest;
        int deletions;
        int additions;
        for (GHIssue issue : pullRequests) {
            repository = issue.getRepository();

            try {
                pullRequest = repository.getPullRequest(issue.getNumber());
                deletions = pullRequest.getDeletions();
                additions = pullRequest.getAdditions();
                user = issue.getUser().getLogin();
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage());
                continue;
            }

            sections.add(
                Section.of(
                    Text.of(
                        TextType.MARKDOWN,
                        String.format(
                            "%s *%s* %nrepository: %s, :heavy_minus_sign: %d :heavy_plus_sign: %d",
                            user,
                            issue.getTitle(),
                            repository.getFullName(),
                            deletions,
                            additions
                        )
                    )
                ).withAccessory(
                    Button.of(
                        Text.of(TextType.PLAIN_TEXT, "Open"),
                        issue.getNodeId()
                    ).withUrl(issue.getHtmlUrl().toString())
                )
            );
        }

        sections.add(Section.of(Text.of(TextType.MARKDOWN, String.format("config id: _%s_", notification.getName()))));

        slackClient.postMessage(
            ChatPostMessageParams.builder()
                    .setBlocks(sections)
                    .setChannelId(notification.getConfig().get("channel").toString())
                    .build()
        ).join();
    }

    private GitHub grabInstallationClient(Long installationId) {
        if (!installationClients.containsKey(installationId)) {
            GHAppInstallationToken token;
            GitHub installationClient;
            try {
                token = installationRepository.get(installationId).createToken().create();
                installationClient = new GitHubBuilder()
                        .withAppInstallationToken(token.getToken())
                        .build();
            } catch (IOException e) {
                throw new RuntimeException(String.format("Could not crate client for installation %d", installationId));
            }

            installationClients.put(installationId, installationClient);

            return installationClient;
        }

        return installationClients.get(installationId);
    }
}
