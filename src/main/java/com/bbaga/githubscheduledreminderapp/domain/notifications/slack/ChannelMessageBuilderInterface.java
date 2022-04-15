package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;
import com.hubspot.slack.client.models.blocks.Block;
import java.util.Map;

public interface ChannelMessageBuilderInterface {
    Block createHeader(String text);
    Block createHeaderIssues(String text, String overflowFormat, int showing, int from);
    Block createHeaderPRs(String text, String overflowFormat, int showing, int from);
    Block createLine(GitHubPullRequest pullRequest, String text, Map<String, String> trackingParams);
    Block createLine(GitHubIssue issue, String text, Map<String, String> trackingParams);
    Block createNoResultsMessage(String text);
}
