package com.bbaga.githubscheduledreminderapp.domain.statistics;

import org.springframework.context.ApplicationEvent;

public class StatisticsEvent extends ApplicationEvent {
    final private String eventName;

    public StatisticsEvent(Object source, String eventName) {
        super(source);
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }

    public static StatisticsEvent create(Object source, String eventName) {
        return new StatisticsEvent(source, eventName);
    }
}
