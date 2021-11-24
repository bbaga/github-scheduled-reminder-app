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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
        ArrayList<GHIssue> issues = data.getIssues();

        List<Block> sections = new ArrayList<>();
        List<Block> issueSections = new ArrayList<>();
        List<Block> prSections = new ArrayList<>();

        sections.add(Header.of(Text.of(TextType.PLAIN_TEXT, "Reporting open issues and pull requests")));

        for (GHIssue issue : issues) {
            try {
                if (issue instanceof GHPullRequest) {
                    prSections.add(getSection(issue));
                    continue;
                }

                issueSections.add(getSection(issue));
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage());
                return;
            }
        }

        if ((issueSections.size() + prSections.size()) == 0) {
            sections.add(markdownSection("*There aren't any open issues or pull requests.*"));
        } else {
            if (issueSections.size() > 0) {
                sections.add(markdownSection("*Open Issues:*"));
                sections.add(Divider.builder().build());
                sections.addAll(issueSections);
            }

            if (prSections.size() > 0) {
                sections.add(markdownSection("*Open Pull Requests:*"));
                sections.add(Divider.builder().build());
                sections.addAll(prSections);
            }
        }

        sections.add(markdownSection("config id: _%s_", notification.getName()));

        slackClient.postMessage(
            ChatPostMessageParams.builder()
                .setBlocks(sections)
                .setChannelId(notification.getConfig().get("channel").toString())
                .build()
        ).join();
    }

    private Block getSection(GHPullRequest pullRequest) throws IOException {

        return markdownSection(
            "%s *%s* %nrepository: %s, :heavy_minus_sign: %d :heavy_plus_sign: %d",
            pullRequest.getUser().getLogin(),
            pullRequest.getTitle(),
            pullRequest.getRepository().getFullName(),
            pullRequest.getDeletions(),
            pullRequest.getAdditions()
        ).withAccessory(linkButton(pullRequest.getNodeId(), pullRequest.getHtmlUrl().toString()));
    }

    private Block getSection(GHIssue issue) throws IOException {

        return markdownSection(
            "%s *%s*%nrepository: %s, age: %s",
            issue.getUser().getLogin(),
            issue.getTitle(),
            issue.getRepository().getFullName(),
            getIssueAge(issue)
        ).withAccessory(linkButton(issue.getNodeId(), issue.getHtmlUrl().toString()));
    }

    private String getIssueAge(GHIssue issue) throws IOException {
        String ageTemplate = "%d day";
        Instant now = Instant.now();
        long units = ChronoUnit.DAYS.between(issue.getCreatedAt().toInstant(), now);

        if (units < 1) {
            units = ChronoUnit.HOURS.between(issue.getCreatedAt().toInstant(), now);
            ageTemplate = "%d hour";
        }

        if (units > 1) {
            ageTemplate = ageTemplate + "s";
        }

        return ageTemplate.formatted(units);
    }

    private Section markdownSection(String pattern, Object... args) {
        return Section.of(Text.of(TextType.MARKDOWN, String.format(pattern, args)));
    }

    private Button linkButton(String id, String url) {
        return Button.of(Text.of(TextType.PLAIN_TEXT, "Open"), id).withUrl(url);
    }
}
