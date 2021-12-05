package com.bbaga.githubscheduledreminderapp.domain.statistics;

import java.io.UnsupportedEncodingException;

public class PassThroughUrlBuilder implements UrlBuilderInterface {
    public String from(String source, String action, String targetUrl) throws UnsupportedEncodingException {
        return targetUrl;
    }
}
