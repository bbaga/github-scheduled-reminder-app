package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;
import com.slack.api.model.block.HeaderBlock;
import com.slack.api.model.block.SectionBlock;
import java.util.Map;

public interface ChannelMessageBuilderInterface {
    HeaderBlock createHeader(String text);
    SectionBlock createHeaderIssues(String text, String overflowFormat, int showing, int from);
    SectionBlock createHeaderPRs(String text, String overflowFormat, int showing, int from);
    SectionBlock createLine(GitHubPullRequest pullRequest, String text, Map<String, String> trackingParams);
    SectionBlock createLine(GitHubIssue issue, String text, Map<String, String> trackingParams);
    SectionBlock createNoResultsMessage(String text);
}
