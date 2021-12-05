package com.bbaga.githubscheduledreminderapp.domain.statistics;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TrackingUrlBuilder implements UrlBuilderInterface {

    private final String endpointUrl;

    public TrackingUrlBuilder(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public String from(String source, String action, String targetUrl) throws UnsupportedEncodingException {
        return String.format(
                "%s/redirect/action?source=%s&action=%s&targetUrl=%s",
                endpointUrl,
                action,
                source,
                URLEncoder.encode(targetUrl, StandardCharsets.UTF_8.toString())
        );
    }
}
