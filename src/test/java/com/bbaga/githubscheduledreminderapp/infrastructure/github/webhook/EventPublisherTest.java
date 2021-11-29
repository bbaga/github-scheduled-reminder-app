package com.bbaga.githubscheduledreminderapp.infrastructure.github.webhook;

import com.bbaga.githubscheduledreminderapp.infrastructure.github.webhook.events.RepositoryInstallationEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.ArgumentMatchers.any;

class EventPublisherTest {

    @Test
    void publishKnownEvent() {
        ApplicationEventPublisher mockApplicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);
        EventPublisher publisher = new EventPublisher();
        publisher.setApplicationEventPublisher(mockApplicationEventPublisher);

        publisher.publishEvent(this, "installation_repositories", "{}");

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        Mockito.verify(mockApplicationEventPublisher, Mockito.times(1)).publishEvent(eventCaptor.capture());

        Assertions.assertTrue(eventCaptor.getValue() instanceof RepositoryInstallationEvent);
    }

    @Test
    void publishUnknownEvent() {
        ApplicationEventPublisher mockApplicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);
        EventPublisher publisher = new EventPublisher();
        publisher.setApplicationEventPublisher(mockApplicationEventPublisher);

        publisher.publishEvent(this, "some_random_type", "{}");

        Mockito.verify(mockApplicationEventPublisher, Mockito.never()).publishEvent(any());
    }
}
