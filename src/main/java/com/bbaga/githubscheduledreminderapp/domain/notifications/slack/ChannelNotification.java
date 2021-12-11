package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.bbaga.githubscheduledreminderapp.domain.configuration.Notification;
import com.bbaga.githubscheduledreminderapp.domain.configuration.SlackNotification;
import com.bbaga.githubscheduledreminderapp.domain.notifications.NotificationInterface;
import com.bbaga.githubscheduledreminderapp.domain.statistics.UrlBuilderInterface;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;
import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.methods.params.chat.ChatPostMessageParams;
import com.hubspot.slack.client.models.blocks.Block;
import com.hubspot.slack.client.models.blocks.Divider;
import com.hubspot.slack.client.models.blocks.Header;
import com.hubspot.slack.client.models.blocks.Section;
import com.hubspot.slack.client.models.blocks.elements.Button;
import com.hubspot.slack.client.models.blocks.objects.Text;
import com.hubspot.slack.client.models.blocks.objects.TextType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ChannelNotification implements NotificationInterface<ChannelNotificationDataProvider.Data> {

    private final SlackClient slackClient;
    private final Logger logger = LoggerFactory.getLogger(ChannelNotification.class);
    private final UrlBuilderInterface urlBuilder;

    public ChannelNotification(SlackClient slackClient, UrlBuilderInterface urlBuilder) {
        this.slackClient = slackClient;
        this.urlBuilder = urlBuilder;
    }

    @Override
    public void send(ChannelNotificationDataProvider.Data data) {
        Notification notification = data.getNotification();
        ArrayList<GitHubIssue> issues = data.getIssues();

        List<Block> sections = new ArrayList<>();
        List<Block> issueSections = new ArrayList<>();
        List<Block> prSections = new ArrayList<>();

        sections.add(Header.of(Text.of(TextType.PLAIN_TEXT, "Reporting open issues and pull requests")));

        for (GitHubIssue issue : issues) {
            try {
                if (issue instanceof GitHubPullRequest) {
                    prSections.add(getSection((GitHubPullRequest) issue));
                    continue;
                }

                issueSections.add(getSection(issue));
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage());
                return;
            }
        }

        int maxNumberOfIssues = 40; // There are 7 static sections and the max allowed is 50 sections
        int maxGroupSize = maxNumberOfIssues / 2;
        boolean mustTruncate = issues.size() > maxNumberOfIssues;
        int issueSectionsSize = issueSections.size();
        int prSectionsSize = prSections.size();

        if ((issueSectionsSize + prSectionsSize) == 0) {
            sections.add(markdownSection("*There aren't any open issues or pull requests.*"));
        } else {
            if (issueSectionsSize > 0) {
                String displayCount = String.format("%d", issueSectionsSize);

                if (mustTruncate && issueSectionsSize > maxGroupSize) {
                    displayCount = String.format("showing last %d out of %d", maxGroupSize, issueSectionsSize);
                    issueSections = getTruncatedGroup(issueSections, issueSectionsSize, maxGroupSize);
                }

                sections.add(markdownSection(String.format("*Open Issues (%s):*", displayCount)));
                sections.add(Divider.builder().build());

                sections.addAll(issueSections);
            }

            if (prSectionsSize > 0) {
                String displayCount = String.format("%d", prSectionsSize);

                if (mustTruncate && prSectionsSize > maxGroupSize) {
                    displayCount = String.format("showing last %d out of %d", maxGroupSize, prSectionsSize);
                    prSections = getTruncatedGroup(prSections, prSectionsSize, maxGroupSize);
                }

                sections.add(markdownSection(String.format("*Open Pull Requests (%s):*", displayCount)));
                sections.add(Divider.builder().build());

                sections.addAll(prSections);
            }
        }

        sections.add(markdownSection("config id: _%s_", notification.getFullName()));

        slackClient.postMessage(
            ChatPostMessageParams.builder()
                .setBlocks(sections)
                .setChannelId(((SlackNotification) notification.getConfig()).getChannel())
                .build()
        ).join();
    }

    private List<Block> getTruncatedGroup(List<Block> sections, int sectionsSize, int maxGroupSize) {
        return sections.subList(sectionsSize - maxGroupSize, sectionsSize);
    }

    private Block getSection(GitHubPullRequest pullRequest) throws IOException {

        String mergeableState = pullRequest.getMergeableState();
        String mergeableEmoji;

        if (List.of("clean", "has_hooks").contains(mergeableState)) {
            mergeableEmoji = ":large_green_circle:";
        } else if (List.of("dirty", "blocked", "behind", "draft").contains(mergeableState)) {
            mergeableEmoji = ":red_circle:";
        } else {
            mergeableEmoji = ":large_yellow_circle:";
        }

        logger.debug(
            String.format(
                "[PR] %s; mergeable-state: %s; created-at: %s; updated-at: %s",
                pullRequest.getHtmlUrl(),
                mergeableState,
                pullRequest.getCreatedAt(),
                pullRequest.getUpdatedAt()
            )
        );

        return markdownSection(
            "%s %s *%s*%nrepository: %s, age: %s, :heavy_minus_sign: %d :heavy_plus_sign: %d",
            mergeableEmoji,
            pullRequest.getUser().getLogin(),
            pullRequest.getTitle(),
            pullRequest.getRepository().getFullName(),
            getIssueAge(pullRequest),
            pullRequest.getDeletions(),
            pullRequest.getAdditions()
        ).withAccessory(linkButton(pullRequest));
    }

    private Block getSection(GitHubIssue issue) throws IOException {
        logger.debug(
                String.format(
                        "[Issue] %s; created-at: %s; updated-at: %s",
                        issue.getHtmlUrl(),
                        issue.getCreatedAt(),
                        issue.getUpdatedAt()
                )
        );

        return markdownSection(
            "%s *%s*%nrepository: %s, age: %s",
            issue.getUser().getLogin(),
            issue.getTitle(),
            issue.getRepository().getFullName(),
            getIssueAge(issue)
        ).withAccessory(linkButton(issue));
    }

    private String getIssueAge(GitHubIssue issue) throws IOException {
        String ageTemplate = "%d day";
        Instant now = Instant.now();
        Date createdAt = issue.getCreatedAt();
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

    private Section markdownSection(String pattern, Object... args) {
        return Section.of(Text.of(TextType.MARKDOWN, String.format(pattern, args)));
    }

    private Button linkButton(GitHubIssue issue) {
        String url;

        try {
            url = urlBuilder.from("slack.channel", issue);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            url = issue.getHtmlUrl().toString();
        }

        return Button.of(Text.of(TextType.PLAIN_TEXT, "Open"), issue.getNodeId()).withUrl(url);
    }
}
