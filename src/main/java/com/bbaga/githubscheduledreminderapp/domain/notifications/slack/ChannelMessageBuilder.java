package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.bbaga.githubscheduledreminderapp.domain.statistics.UrlBuilderInterface;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubUser;
import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;
import static com.slack.api.model.block.element.BlockElements.*;

import com.slack.api.model.block.HeaderBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.element.ButtonElement;
import java.util.Map;
import java.util.regex.Matcher;
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
    public HeaderBlock createHeader(String text) {
        return header(h -> h.text(plainText(text)));
    }

    @Override
    public SectionBlock createHeaderIssues(String text, String overflowFormat, int showing, int from) {
        String counter;

        if (showing < from) {
            counter = overflowFormat.replace("\\$showing", String.valueOf(showing))
                                .replace("\\$from", String.valueOf(from));
        } else {
            counter = String.valueOf(showing);
        }

        return markdownSection(text.replace("\\$counter", counter));
    }

    @Override
    public SectionBlock createHeaderPRs(String text, String overflowFormat, int showing, int from) {
        String counter;

        if (showing < from) {
            counter = overflowFormat.replace("\\$showing", String.valueOf(showing))
                    .replace("\\$from", String.valueOf(showing));
        } else {
            counter = String.valueOf(showing);
        }

        return markdownSection(text.replace("\\$counter", counter));
    }

    @Override
    public SectionBlock createNoResultsMessage(String text) {
        return markdownSection(text);
    }

    @Override
    public SectionBlock createLine(GitHubPullRequest pullRequest, String text, Map<String, String> trackingParams) {
        String login;
        String mergeableState = "";
        String mergeableEmoji;
        int deletions;
        int additions;

        try {
            mergeableState = pullRequest.getMergeableState();
        } catch (IOException ignored) {
            //Ignored Exception
        }

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

        var processedText = text.replace("\\$login", login)
            .replace("\\$mergeableEmoji", Matcher.quoteReplacement(mergeableEmoji))
            .replace("\\$title", Matcher.quoteReplacement(pullRequest.getTitle()))
            .replace("\\$repository", pullRequest.getRepository().getFullName())
            .replace("\\$age", getIssueAge(pullRequest))
            .replace("\\$deletions", String.valueOf(deletions))
            .replace("\\$additions", String.valueOf(additions))
            .replace("\\$link", getUrl(pullRequest, trackingParams))
            .replace("\\$assignee-logins", getAssigneeLogins(pullRequest))
            .replace("\\$assignee-login-links", getAssigneeLoginLinks(pullRequest))
            .replace("\\$reviewer-logins", getRequestedReviewersLogins(pullRequest))
            .replace("\\$reviewer-login-links", getRequestedReviewersLoginLinks(pullRequest));

        if (processedText.contains("$button")) {
            processedText = processedText.replace("\\$button", "");
            var section = markdownSection(processedText);
            section.setAccessory(linkButton(pullRequest, trackingParams));

            return section;
        }

        return markdownSection(processedText);
    }

    @Override
    public SectionBlock createLine(GitHubIssue issue, String text, Map<String, String> trackingParams) {
        String login;

        try {
            login = issue.getUser().getLogin();
        } catch (IOException exception) {
            login = "Unknown";
        }

        var processedText = text.replace("\\$login", login)
                .replace("\\$title", issue.getTitle())
                .replace("\\$repository", issue.getRepository().getFullName())
                .replace("\\$age", getIssueAge(issue))
                .replace("\\$assignee-logins", getAssigneeLogins(issue))
                .replace("\\$assignee-login-links", getAssigneeLoginLinks(issue))
                .replace("\\$link", getUrl(issue, trackingParams));

        if (processedText.contains("$button")) {
            processedText = processedText.replace("\\$button", "");
            var section = markdownSection(processedText);
            section.setAccessory(linkButton(issue, trackingParams));

            return section;
        }

        return markdownSection(processedText);
    }

    private SectionBlock markdownSection(String markdown) {
        return section(s -> s.text(markdownText(markdown)));
    }

    private ButtonElement linkButton(GitHubIssue issue, Map<String, String> trackingParams) {
        String url = getUrl(issue, trackingParams);

        return button(b -> b.text(plainText(pt -> pt.text("Open"))).value(issue.getNodeId()).url(url));
    }

    private String getUrl(GitHubIssue issue, Map<String, String> trackingParams) {
        String urlString;

        try {
            var urlBuilderCopy = urlBuilder.copy();
            trackingParams.forEach(urlBuilderCopy::setExtraParameter);
            urlString = urlBuilderCopy.from("slack.channel", issue);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            urlString = issue.getHtmlUrl().toString();
        }
        return urlString;
    }

    private String getAssigneeLogins(GitHubIssue issue) {
        return issue.getAssignees().stream().map(GitHubUser::getLogin).collect(Collectors.joining(", "));
    }

    private String getRequestedReviewersLogins(GitHubPullRequest pullRequest) {
        try {
            return pullRequest.getRequestedReviewers().stream().map(GitHubUser::getLogin)
                .collect(Collectors.joining(", "));
        } catch (IOException ignore) {
            //Ignored Exception
            return "";
        }
    }

    private String getRequestedReviewersLoginLinks(GitHubPullRequest pullRequest) {
        try {
            return pullRequest.getRequestedReviewers().stream()
                .map(user -> "<" + user.getHtmlUrl() + "|" + user.getLogin() + ">").collect(Collectors.joining(", "));
        } catch (IOException ignore) {
            //Ignored Exception
            return "";
        }
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
