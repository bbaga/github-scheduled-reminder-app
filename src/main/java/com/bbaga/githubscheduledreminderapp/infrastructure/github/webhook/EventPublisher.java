package com.bbaga.githubscheduledreminderapp.infrastructure.github.webhook;

import com.bbaga.githubscheduledreminderapp.infrastructure.github.webhook.events.RepositoryInstallationEvent;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.webhook.events.WebhookEventInterface;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.HashMap;
import java.util.function.BiFunction;

public class EventPublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher eventPublisher;

    private final HashMap<String, BiFunction<Object, String, WebhookEventInterface>> eventFactory = new HashMap<>();

    public EventPublisher() {
        eventFactory.put("installation_repositories", RepositoryInstallationEvent::create);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        eventPublisher = applicationEventPublisher;
    }

    public void publishEvent(Object source, String type, String body) {
        if (eventFactory.containsKey(type)) {
            eventPublisher.publishEvent(eventFactory.get(type).apply(source, body));
        }
    }
}
