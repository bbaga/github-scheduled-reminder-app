package com.bbaga.githubscheduledreminderapp.domain.statistics;

import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;

import java.io.UnsupportedEncodingException;

public class PassThroughUrlBuilder implements UrlBuilderInterface {
    @Override
    public UrlBuilderInterface copy() {
        return new PassThroughUrlBuilder();
    }

    @Override
    public UrlBuilderInterface setExtraParameter(String name, String value) {
        return this;
    }

    public String from(String source, GitHubIssue issue) throws UnsupportedEncodingException {
        return issue.getHtmlUrl().toString();
    }
}
