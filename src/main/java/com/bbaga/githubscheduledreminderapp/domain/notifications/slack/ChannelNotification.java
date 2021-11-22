package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.bbaga.githubscheduledreminderapp.domain.configuration.Notification;
import com.bbaga.githubscheduledreminderapp.domain.notifications.NotificationInterface;
import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.methods.params.chat.ChatPostMessageParams;
import com.hubspot.slack.client.models.blocks.Block;
import com.hubspot.slack.client.models.blocks.Divider;
import com.hubspot.slack.client.models.blocks.Header;
import com.hubspot.slack.client.models.blocks.Section;
import com.hubspot.slack.client.models.blocks.elements.Button;
import com.hubspot.slack.client.models.blocks.objects.Text;
import com.hubspot.slack.client.models.blocks.objects.TextType;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ChannelNotification implements NotificationInterface<ChannelNotificationDataProvider.Data> {

    private final SlackClient slackClient;
    private final Logger logger = LoggerFactory.getLogger(ChannelNotification.class);

    public ChannelNotification(SlackClient slackClient) {
        this.slackClient = slackClient;
    }

    @Override
    public void send(ChannelNotificationDataProvider.Data data) {
        Notification notification = data.getNotification();
        Set<GHIssue> issues = data.getIssues();
        Set<GHIssue> pullRequests = data.getPullRequests();

        List<Block> sections = new ArrayList<>();
        sections.add(Header.of(Text.of(TextType.PLAIN_TEXT, "Reporting open issues and pull requests")));

        if ((issues.size() + pullRequests.size()) == 0) {
            sections.add(markdownSection("*There aren't any open issues or pull requests.*"));
        }

        if (issues.size() > 0) {
            sections.add(markdownSection("*Open Issues:*"));
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
                markdownSection(
                    "%s *%s*%nrepository: %s",
                    user,
                    issue.getTitle(),
                    issue.getRepository().getFullName()
                ).withAccessory(linkButton(issue.getNodeId(), issue.getHtmlUrl().toString()))
            );
        }

        if (pullRequests.size() > 0) {
            sections.add(markdownSection("*Open Pull Requests:*"));
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
                markdownSection(
                    "%s *%s* %nrepository: %s, :heavy_minus_sign: %d :heavy_plus_sign: %d",
                    user,
                    issue.getTitle(),
                    repository.getFullName(),
                    deletions,
                    additions
                ).withAccessory(linkButton(issue.getNodeId(), issue.getHtmlUrl().toString()))
            );
        }

        sections.add(markdownSection("config id: _%s_", notification.getName()));

        slackClient.postMessage(
            ChatPostMessageParams.builder()
                .setBlocks(sections)
                .setChannelId(notification.getConfig().get("channel").toString())
                .build()
        ).join();
    }

    private Section markdownSection(String pattern, Object... args) {
        return Section.of(Text.of(TextType.MARKDOWN, String.format(pattern, args)));
    }

    private Button linkButton(String id, String url) {
        return Button.of(Text.of(TextType.PLAIN_TEXT, "Open"), id).withUrl(url);
    }
}
