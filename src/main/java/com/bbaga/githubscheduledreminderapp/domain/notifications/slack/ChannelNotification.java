package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import static com.slack.api.model.block.Blocks.divider;
import static com.slack.api.model.block.Blocks.section;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;

import com.bbaga.githubscheduledreminderapp.domain.configuration.Notification;
import com.bbaga.githubscheduledreminderapp.domain.configuration.NotificationConfigurationInterface;
import com.bbaga.githubscheduledreminderapp.domain.configuration.SlackNotificationConfiguration;
import com.bbaga.githubscheduledreminderapp.domain.configuration.template.TemplateConfig;
import com.bbaga.githubscheduledreminderapp.domain.notifications.NotificationInterface;
import com.bbaga.githubscheduledreminderapp.domain.statistics.StatisticsEvent;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.search.SearchMessagesRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.users.UsersInfoResponse;
import com.slack.api.model.block.HeaderBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.TextObject;
import java.io.IOException;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChannelNotification implements NotificationInterface<ChannelNotificationDataProvider.Data> {
    private final MethodsClient slackClient;
    private final ChannelMessageBuilderInterface messageBuilder;
    private final ApplicationEventPublisher eventPublisher;

    private final Logger logger = LoggerFactory.getLogger(ChannelNotification.class);

    public ChannelNotification(
        @Qualifier("slack.app")MethodsClient slackClient,
        ChannelMessageBuilderInterface messageBuilder,
        ApplicationEventPublisher eventPublisher
    ) {
        this.slackClient = slackClient;
        this.messageBuilder = messageBuilder;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void send(ChannelNotificationDataProvider.Data data) {
        Notification notification = data.getNotification();
        List<GitHubIssue> issues = data.getIssues();

        NotificationConfigurationInterface config = notification.getConfig();

        if (!(config instanceof SlackNotificationConfiguration)) {
            throw new RuntimeException("Invalid configuration type passed to Slack Channel Notification");
        }

        var slackConfig = ((SlackNotificationConfiguration) config);
        var templateConfig = slackConfig.getTemplateConfig();

        if (templateConfig == null) {
            templateConfig = new TemplateConfig();
        }

        if (templateConfig.getMode() == null) {
            templateConfig.setMode(TemplateConfig.MODE_BLOCK);
        }

        if (templateConfig.getHeaderMain() == null) {
            templateConfig.setHeaderMain("Reporting open issues and pull requests");
        }

        if (templateConfig.getHeaderIssues() == null) {
            templateConfig.setHeaderIssues("*Open Issues ($counter):*");
        }

        if (templateConfig.getHeaderPRs() == null) {
            templateConfig.setHeaderPRs("*Open Pull Requests ($counter):*");
        }

        if (templateConfig.getNoResults() == null) {
            templateConfig.setNoResults("*There aren't any open issues or pull requests.*");
        }

        if (templateConfig.getSkipNoResults() == null) {
            templateConfig.setSkipNoResults(false);
        }

        if (templateConfig.getLineIssues() == null) {
            templateConfig.setLineIssues("$login *$title*\nrepository: $repository, age: $age$button");
        }

        if (templateConfig.getLinePRs() == null) {
            templateConfig.setLinePRs("$mergeableEmoji $login *$title*\nrepository: $repository, age: $age, :heavy_minus_sign: $deletions :heavy_plus_sign: $additions$button");
        }

        if (templateConfig.getOverflowFormat() == null) {
            templateConfig.setOverflowFormat("$showing of $from");
        }

        if (templateConfig.getDeleteOldMessages() == null) {
            templateConfig.setDeleteOldMessages(false);
        }

        List<LayoutBlock> sections = new ArrayList<>();
        List<LayoutBlock> issueSections = new ArrayList<>();
        List<LayoutBlock> prSections = new ArrayList<>();

        sections.add(messageBuilder.createHeader(templateConfig.getHeaderMain()));

        var slackChannel = slackConfig.getChannel();

        var trackingParams = new HashMap<String, String>();
        trackingParams.put("channel", slackChannel);

        for (GitHubIssue issue : issues) {
            if (issue instanceof GitHubPullRequest) {
                prSections.add(
                    messageBuilder.createLine((GitHubPullRequest) issue, templateConfig.getLinePRs(), trackingParams)
                );

                eventPublisher.publishEvent(StatisticsEvent.create(
                    this,
                    "notification."+notification.getFullName()+".slack.channel.pull-request.found")
                );

                continue;
            }

            issueSections.add(messageBuilder.createLine(issue, templateConfig.getLineIssues(), trackingParams));
            eventPublisher.publishEvent(StatisticsEvent.create(
                this,
                "notification."+notification.getFullName()+".slack.channel.issue.found")
            );
        }

        int maxNumberOfIssues = 40; // There are 7 static sections and the max allowed is 50 sections
        int maxGroupSize = maxNumberOfIssues / 2;
        boolean mustTruncate = issues.size() > maxNumberOfIssues;
        int issueSectionsSize = issueSections.size();
        int prSectionsSize = prSections.size();

        if ((issueSectionsSize + prSectionsSize) == 0) {
            if (templateConfig.getSkipNoResults().equals(true)) {
                return;
            }

            sections.add(messageBuilder.createNoResultsMessage(templateConfig.getNoResults()));
        } else {
            if (issueSectionsSize > 0) {
                int displayCount = issueSectionsSize;

                if (mustTruncate && issueSectionsSize > maxGroupSize) {
                    displayCount = maxGroupSize;
                    issueSections = getTruncatedGroup(issueSections, issueSectionsSize, maxGroupSize);
                }

                eventPublisher.publishEvent(StatisticsEvent.create(
                    this,
                    "notification."+notification.getFullName()+".slack.channel.issue.displayed",
                    displayCount
                ));

                sections.add(
                    messageBuilder.createHeaderIssues(
                        templateConfig.getHeaderIssues(),
                        templateConfig.getOverflowFormat(),
                        displayCount,
                        issueSectionsSize
                    )
                );
                sections.add(divider());

                sections.addAll(issueSections);
            }

            if (prSectionsSize > 0) {
                int displayCount = prSectionsSize;

                if (mustTruncate && prSectionsSize > maxGroupSize) {
                    displayCount = maxGroupSize;
                    prSections = getTruncatedGroup(prSections, prSectionsSize, maxGroupSize);
                }

                eventPublisher.publishEvent(StatisticsEvent.create(
                    this,
                    "notification."+notification.getFullName()+".slack.channel.pull-request.displayed",
                    displayCount
                ));

                sections.add(
                        messageBuilder.createHeaderIssues(
                                templateConfig.getHeaderPRs(),
                                templateConfig.getOverflowFormat(),
                                displayCount,
                                prSectionsSize
                        )
                );
                sections.add(divider());

                sections.addAll(prSections);
            }
        }

        String configIdLine = String.format("config id: _%s_", notification.getFullName());
        sections.add(section(s -> s.text(markdownText(configIdLine))));

        var requestBuilder = ChatPostMessageRequest.builder()
            .channel(slackChannel);

        if (templateConfig.getMode().equals(TemplateConfig.MODE_FREE_TEXT)) {
            List<String> textPieces = sections.stream().map(block -> {
                TextObject text = null;

                if (block instanceof HeaderBlock) {
                    text = ((HeaderBlock) block).getText();
                }

                if (block instanceof SectionBlock) {
                    text = ((SectionBlock) block).getText();
                }

                if (text != null) {
                    return text.getText();
                }

                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());

            requestBuilder.text(String.join("\n", textPieces));
        }

        if (templateConfig.getMode().equals(TemplateConfig.MODE_BLOCK)) {
            requestBuilder.blocks(sections);
        }

        eventPublisher.publishEvent(
            StatisticsEvent.create(
                this,
                "notification."+notification.getFullName()+".slack.channel.posted"
            )
        );
        ChatPostMessageResponse response;

        try {
            response = slackClient.chatPostMessage(requestBuilder.build());
        } catch (SlackApiException | IOException apiException) {
            logger.error(apiException.getLocalizedMessage());
            return;
        }

        if (!templateConfig.getDeleteOldMessages()) {
            return;
        }

        if (!response.isOk()) {
            logger.error(response.getError());
            return;
        }

        var message = response.getMessage();
        var userId = message.getUser();

        var timestampParts = message.getTs().split("\\.");
        long seconds, nanoAdjustment;
        Instant deleteMessagesBefore;

        if (timestampParts.length > 0) {
            seconds = Long.parseLong(timestampParts[0]);
            nanoAdjustment = timestampParts.length > 1 ? Long.parseLong(timestampParts[1]) : 0;

            deleteMessagesBefore = Instant.ofEpochSecond(seconds, nanoAdjustment);
        } else {
            // how come there is nothing?
            deleteMessagesBefore = Instant.now();
        }

        // Setting offset to 5 minutes
        deleteMessagesBefore = deleteMessagesBefore.minusSeconds(300);

        UsersInfoResponse userInfoResponse;

        try {
            userInfoResponse = slackClient.usersInfo(req -> req.user(userId));
        } catch (IOException | SlackApiException e) {
            logger.error(e.getLocalizedMessage());
            return;
        }

        if (!userInfoResponse.isOk()) {
            logger.error(userInfoResponse.getError());
        }

        Instant finalDeleteMessagesBefore = deleteMessagesBefore;
        var user = userInfoResponse.getUser();

        if (user.getName().isEmpty()) {
            logger.debug("Bot's username could not be identified. Skipping old message removal.");
        }

        var searchRequest = SearchMessagesRequest.builder()
            .query("in:" + slackChannel + " from:" + user.getName())
            .count(25)
            .sort("timestamp")
            .sortDir("desc")
            .build();

        eventPublisher.publishEvent(SearchAndDeleteEvent.create(this, searchRequest, finalDeleteMessagesBefore));
    }

    private List<LayoutBlock> getTruncatedGroup(List<LayoutBlock> sections, int sectionsSize, int maxGroupSize) {
        return sections.subList(sectionsSize - maxGroupSize, sectionsSize);
    }
}
