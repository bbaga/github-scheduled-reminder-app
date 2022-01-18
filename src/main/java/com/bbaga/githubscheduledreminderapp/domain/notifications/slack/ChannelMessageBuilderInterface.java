package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;
import com.hubspot.slack.client.models.blocks.Block;

public interface ChannelMessageBuilderInterface {
    public Block createHeader(String text);
    public Block createHeaderIssues(String text, String overflowFormat, int showing, int from);
    public Block createHeaderPRs(String text, String overflowFormat, int showing, int from);
    public Block createLine(GitHubPullRequest pullRequest, String text);
    public Block createLine(GitHubIssue issue, String text);
    public Block createNoResultsMessage(String text);
}
