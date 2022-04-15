package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.bbaga.githubscheduledreminderapp.domain.configuration.Notification;
import com.bbaga.githubscheduledreminderapp.domain.configuration.NotificationConfigurationInterface;
import com.bbaga.githubscheduledreminderapp.domain.configuration.SlackNotificationConfiguration;
import com.bbaga.githubscheduledreminderapp.domain.configuration.template.TemplateConfig;
import com.bbaga.githubscheduledreminderapp.domain.notifications.NotificationInterface;
import com.bbaga.githubscheduledreminderapp.domain.statistics.StatisticsEvent;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;
import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.methods.params.chat.ChatPostMessageParams;
import com.hubspot.slack.client.methods.params.search.SearchMessagesParams;
import com.hubspot.slack.client.models.blocks.*;
import com.hubspot.slack.client.models.blocks.objects.Text;
import com.hubspot.slack.client.models.blocks.objects.TextType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChannelNotification implements NotificationInterface<ChannelNotificationDataProvider.Data> {

    private final SlackClient slackClient;
    private final Optional<SlackClient> slackUserClient;
    private final ChannelMessageBuilderInterface messageBuilder;
    private final ApplicationEventPublisher eventPublisher;

    public ChannelNotification(
        @Qualifier("slack.app")SlackClient slackClient,
        @Qualifier("slack.user")Optional<SlackClient> slackUserClient,
        ChannelMessageBuilderInterface messageBuilder,
        ApplicationEventPublisher eventPublisher
    ) {
        this.slackClient = slackClient;
        this.slackUserClient = slackUserClient;
        this.messageBuilder = messageBuilder;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void send(ChannelNotificationDataProvider.Data data) {
        Notification notification = data.getNotification();
        List<GitHubIssue> issues = data.getIssues();
        TemplateConfig templateConfig = null;

        NotificationConfigurationInterface config = notification.getConfig();

        if (config instanceof SlackNotificationConfiguration) {
            templateConfig = ((SlackNotificationConfiguration) config).getTemplateConfig();
        }

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

        List<Block> sections = new ArrayList<>();
        List<Block> issueSections = new ArrayList<>();
        List<Block> prSections = new ArrayList<>();

        sections.add(Header.of(Text.of(TextType.PLAIN_TEXT, templateConfig.getHeaderMain())));

        for (GitHubIssue issue : issues) {
            if (issue instanceof GitHubPullRequest) {
                prSections.add(messageBuilder.createLine((GitHubPullRequest) issue, templateConfig.getLinePRs()));
                eventPublisher.publishEvent(StatisticsEvent.create(this, "notification."+notification.getFullName()+".slack.channel.pull-request.found"));
                continue;
            }

            issueSections.add(messageBuilder.createLine(issue, templateConfig.getLineIssues()));
            eventPublisher.publishEvent(StatisticsEvent.create(this, "notification."+notification.getFullName()+".slack.channel.issue.found"));
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
                    this, "notification."+notification.getFullName()+".slack.channel.pull-request.displayed")
                );

                sections.add(
                    messageBuilder.createHeaderIssues(
                        templateConfig.getHeaderIssues(),
                        templateConfig.getOverflowFormat(),
                        displayCount,
                        issueSectionsSize
                    )
                );
                sections.add(Divider.builder().build());

                sections.addAll(issueSections);
            }

            if (prSectionsSize > 0) {
                int displayCount = prSectionsSize;

                if (mustTruncate && prSectionsSize > maxGroupSize) {
                    displayCount = maxGroupSize;
                    prSections = getTruncatedGroup(prSections, prSectionsSize, maxGroupSize);
                }

                eventPublisher.publishEvent(StatisticsEvent.create(
                    this, "notification."+notification.getFullName()+".slack.channel.issue.displayed")
                );

                sections.add(
                        messageBuilder.createHeaderIssues(
                                templateConfig.getHeaderPRs(),
                                templateConfig.getOverflowFormat(),
                                displayCount,
                                prSectionsSize
                        )
                );
                sections.add(Divider.builder().build());

                sections.addAll(prSections);
            }
        }

        String configIdLine = String.format("config id: _%s_", notification.getFullName());
        sections.add(Section.of(Text.of(TextType.MARKDOWN, configIdLine)));

        ChatPostMessageParams.Builder paramBuilder = ChatPostMessageParams.builder();

        if (templateConfig.getMode().equals(TemplateConfig.MODE_FREE_TEXT)) {
            List<String> textPieces = sections.stream().map(block -> {
                Text text = null;

                if (block instanceof Header) {
                    text = ((Header) block).getText();
                }

                if (block instanceof SectionIF) {
                    text = ((SectionIF) block).getText();
                }

                if (text != null) {
                    return text.getText();
                }

                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());

            paramBuilder = paramBuilder.setText(String.join("\n", textPieces));
        } else {
            paramBuilder = paramBuilder.setBlocks(sections);
        }

        eventPublisher.publishEvent(StatisticsEvent.create(this, "notification."+notification.getFullName()+".slack.channel.posted"));

        slackClient.postMessage(
            paramBuilder
            .setChannelId(((SlackNotificationConfiguration) notification.getConfig()).getChannel())
            .build()
        ).join();
    }

    private List<Block> getTruncatedGroup(List<Block> sections, int sectionsSize, int maxGroupSize) {
        return sections.subList(sectionsSize - maxGroupSize, sectionsSize);
    }
}
