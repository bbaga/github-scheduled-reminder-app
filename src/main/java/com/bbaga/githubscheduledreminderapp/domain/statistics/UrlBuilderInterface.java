package com.bbaga.githubscheduledreminderapp.domain.statistics;

import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;

import java.io.UnsupportedEncodingException;

public interface UrlBuilderInterface {
    UrlBuilderInterface copy();
    UrlBuilderInterface setExtraParameter(String name, String value);
    String from(String source, GitHubIssue issue) throws UnsupportedEncodingException;
}
