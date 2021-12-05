package com.bbaga.githubscheduledreminderapp.domain.statistics;

import java.io.UnsupportedEncodingException;

public interface UrlBuilderInterface {
    String from(String source, String action, String targetUrl) throws UnsupportedEncodingException;
}
