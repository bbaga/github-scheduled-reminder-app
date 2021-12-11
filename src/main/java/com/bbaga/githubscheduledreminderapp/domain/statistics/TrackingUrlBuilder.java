package com.bbaga.githubscheduledreminderapp.domain.statistics;

import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TrackingUrlBuilder implements UrlBuilderInterface {

    private final String endpointUrl;

    public TrackingUrlBuilder(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public String from(String source, GitHubIssue issue) throws UnsupportedEncodingException {
        String action = (issue instanceof GitHubPullRequest ? "pull-request" : "issue") + ".view";
        action += String.format(".%s.%s", issue.getRepository().getFullName(), issue.getNumber());
        String targetUrl = issue.getHtmlUrl().toString();

        return String.format(
                "%s/redirect/action?source=%s&action=%s&targetUrl=%s",
                endpointUrl,
                source,
                URLEncoder.encode(action, StandardCharsets.UTF_8.toString()),
                URLEncoder.encode(targetUrl, StandardCharsets.UTF_8.toString())
        );
    }
}
