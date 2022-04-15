package com.bbaga.githubscheduledreminderapp.domain.statistics;

import org.springframework.context.ApplicationEvent;

public class StatisticsEvent extends ApplicationEvent {
    final private String eventName;
    final private int counter;

    public StatisticsEvent(Object source, String eventName) {
        super(source);
        this.eventName = eventName;
        this.counter = 1;
    }

    public StatisticsEvent(Object source, String eventName, int counter) {
        super(source);
        this.eventName = eventName;
        this.counter = counter;
    }

    public String getEventName() {
        return eventName;
    }

    public int getCounter() {
        return counter;
    }

    public static StatisticsEvent create(Object source, String eventName) {
        return new StatisticsEvent(source, eventName);
    }

    public static StatisticsEvent create(Object source, String eventName, int counter) {
        return new StatisticsEvent(source, eventName, counter);
    }
}
