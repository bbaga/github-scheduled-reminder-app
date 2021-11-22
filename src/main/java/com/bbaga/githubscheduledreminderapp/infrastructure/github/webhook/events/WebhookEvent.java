package com.bbaga.githubscheduledreminderapp.infrastructure.github.webhook.events;

import org.springframework.context.ApplicationEvent;

public class WebhookEvent extends ApplicationEvent implements WebhookEventInterface {
    private String body;

    public WebhookEvent(Object source, String body) {
        super(source);

        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public static WebhookEventInterface create(Object source, String body) {
        return new WebhookEvent(source, body);
    }
}
