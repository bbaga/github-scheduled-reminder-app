package com.bbaga.githubscheduledreminderapp.domain.statistics;

import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TrackingUrlBuilder implements UrlBuilderInterface {

    private final String endpointUrl;

    private final Map<String, String> extraParameters = new HashMap<>();

    public TrackingUrlBuilder(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    @Override
    public UrlBuilderInterface copy() {
        return new TrackingUrlBuilder(endpointUrl);
    }

    @Override
    public UrlBuilderInterface setExtraParameter(String name, String value) {
        this.extraParameters.put(name, value);
        return this;
    }

    public String from(String source, GitHubIssue issue) throws UnsupportedEncodingException {
        String action = (issue instanceof GitHubPullRequest ? "pull-request" : "issue") + ".view";
        action += String.format(".%s.%s", issue.getRepository().getFullName(), issue.getNumber());
        String targetUrl = issue.getHtmlUrl().toString();

        StringBuilder url = new StringBuilder(String.format(
            "%s/redirect/action?source=%s&action=%s&targetUrl=%s",
            endpointUrl,
            source,
            URLEncoder.encode(action, StandardCharsets.UTF_8.toString()),
            URLEncoder.encode(targetUrl, StandardCharsets.UTF_8.toString())
        ));

        if (!extraParameters.isEmpty()) {
            for (var record : extraParameters.entrySet()) {
                url.append(String.format("&%s=%s", record.getKey(), record.getValue()));
            }
        }

        return url.toString();
    }
}
