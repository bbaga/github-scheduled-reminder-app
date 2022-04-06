package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.bbaga.githubscheduledreminderapp.domain.statistics.UrlBuilderInterface;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubUser;
import com.hubspot.slack.client.models.blocks.Block;
import com.hubspot.slack.client.models.blocks.Header;
import com.hubspot.slack.client.models.blocks.Section;
import com.hubspot.slack.client.models.blocks.elements.Button;
import com.hubspot.slack.client.models.blocks.objects.Text;
import com.hubspot.slack.client.models.blocks.objects.TextType;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public class ChannelMessageBuilder implements ChannelMessageBuilderInterface {
    private final Logger logger = LoggerFactory.getLogger(ChannelMessageBuilder.class);
    private final UrlBuilderInterface urlBuilder;

    public ChannelMessageBuilder(UrlBuilderInterface urlBuilder) {
        this.urlBuilder = urlBuilder;
    }

    @Override
    public Block createHeader(String text) {
        return Header.of(Text.of(TextType.PLAIN_TEXT, text));
    }

    @Override
    public Block createHeaderIssues(String text, String overflowFormat, int showing, int from) {
        String counter;

        if (showing < from) {
            counter = overflowFormat.replaceAll("\\$showing", String.valueOf(showing))
                                .replaceAll("\\$from", String.valueOf(from));
        } else {
            counter = String.valueOf(showing);
        }

        return markdownSection(text.replaceAll("\\$counter", counter));
    }

    @Override
    public Block createHeaderPRs(String text, String overflowFormat, int showing, int from) {
        String counter;

        if (showing < from) {
            counter = overflowFormat.replaceAll("\\$showing", String.valueOf(showing))
                    .replaceAll("\\$from", String.valueOf(showing));
        } else {
            counter = String.valueOf(showing);
        }

        return markdownSection(text.replaceAll("\\$counter", counter));
    }

    @Override
    public Block createNoResultsMessage(String text) {
        return markdownSection(text);
    }

    @Override
    public Block createLine(GitHubPullRequest pullRequest, String text) {
        String login;
        String mergeableState = "";
        String mergeableEmoji;
        int deletions;
        int additions;

        try {
            mergeableState = pullRequest.getMergeableState();
        } catch (IOException ignored) {}

        if (List.of("clean", "has_hooks").contains(mergeableState)) {
            mergeableEmoji = ":large_green_circle:";
        } else if (List.of("dirty", "blocked", "behind", "draft").contains(mergeableState)) {
            mergeableEmoji = ":red_circle:";
        } else {
            mergeableEmoji = ":large_yellow_circle:";
        }

        try {
            login = pullRequest.getUser().getLogin();
        } catch (IOException exception) {
            login = "Unknown";
        }

        try {
            additions = pullRequest.getAdditions();
        } catch (IOException exception) {
            additions = 0;
        }

        try {
            deletions = pullRequest.getDeletions();
        } catch (IOException exception) {
            deletions = 0;
        }

        text = text.replaceAll("\\$login", login)
                .replaceAll("\\$mergeableEmoji", mergeableEmoji)
                .replaceAll("\\$title", pullRequest.getTitle())
                .replaceAll("\\$repository", pullRequest.getRepository().getFullName())
                .replaceAll("\\$age", getIssueAge(pullRequest))
                .replaceAll("\\$deletions", String.valueOf(deletions))
                .replaceAll("\\$additions", String.valueOf(additions))
                .replaceAll("\\$link", getUrl(pullRequest))
                .replaceAll("\\$assignee-logins", getAssigneeLogins(pullRequest))
                .replaceAll("\\$assignee-login-links", getAssigneeLoginLinks(pullRequest));

        if (text.contains("$button")) {
            text = text.replaceAll("\\$button", "");
            return markdownSection(text).withAccessory(linkButton(pullRequest));
        }

        return markdownSection(text);
    }

    @Override
    public Block createLine(GitHubIssue issue, String text) {
        String login;

        try {
            login = issue.getUser().getLogin();
        } catch (IOException exception) {
            login = "Unknown";
        }

        text = text.replaceAll("\\$login", login)
                .replaceAll("\\$title", issue.getTitle())
                .replaceAll("\\$repository", issue.getRepository().getFullName())
                .replaceAll("\\$age", getIssueAge(issue))
                .replaceAll("\\$assignee-logins", getAssigneeLogins(issue))
                .replaceAll("\\$assignee-login-links", getAssigneeLoginLinks(issue))
                .replaceAll("\\$link", getUrl(issue));

        if (text.contains("$button")) {
            text = text.replaceAll("\\$button", "");
            return markdownSection(text).withAccessory(linkButton(issue));
        }

        return markdownSection(text);
    }

    private Section markdownSection(String markdown) {
        return Section.of(Text.of(TextType.MARKDOWN, markdown));
    }

    private Button linkButton(GitHubIssue issue) {
        String url = getUrl(issue);

        return Button.of(Text.of(TextType.PLAIN_TEXT, "Open"), issue.getNodeId()).withUrl(url);
    }

    private String getUrl(GitHubIssue issue) {
        String url;

        try {
            url = urlBuilder.from("slack.channel", issue);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            url = issue.getHtmlUrl().toString();
        }
        return url;
    }

    private String getAssigneeLogins(GitHubIssue issue) {
        return issue.getAssignees().stream().map(GitHubUser::getLogin).collect(Collectors.joining(", "));
    }

    private String getAssigneeLoginLinks(GitHubIssue issue) {
        return issue.getAssignees().stream().map(user -> "<"+user.getHtmlUrl()+"|"+user.getLogin()+">").collect(Collectors.joining(", "));
    }

    private String getIssueAge(GitHubIssue issue) {
        String ageTemplate = "%d day";
        Instant now = Instant.now();
        Date createdAt;

        try {
            createdAt = issue.getCreatedAt();
        } catch (IOException exception) {
            return "unknown";
        }

        long units = ChronoUnit.DAYS.between(createdAt.toInstant(), now);

        if (units < 1) {
            units = ChronoUnit.HOURS.between(createdAt.toInstant(), now);
            ageTemplate = "%d hour";
        }

        if (units > 1) {
            ageTemplate = ageTemplate + "s";
        }

        return String.format(ageTemplate, units);
    }
}
