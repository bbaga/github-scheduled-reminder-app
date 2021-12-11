package com.bbaga.githubscheduledreminderapp.domain.statistics;

import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;

import java.io.UnsupportedEncodingException;

public interface UrlBuilderInterface {
    String from(String source, GitHubIssue issue) throws UnsupportedEncodingException;
}
