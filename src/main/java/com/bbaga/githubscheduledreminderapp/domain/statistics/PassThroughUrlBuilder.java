package com.bbaga.githubscheduledreminderapp.domain.statistics;

import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;

import java.io.UnsupportedEncodingException;

public class PassThroughUrlBuilder implements UrlBuilderInterface {
    public String from(String source, GitHubIssue issue) throws UnsupportedEncodingException {
        return issue.getHtmlUrl().toString();
    }
}
