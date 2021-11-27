package com.bbaga.githubscheduledreminderapp.domain.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    private Notification<SlackNotification> notification;

    @BeforeEach
    void setUp() {
        notification = new Notification<SlackNotification>();
    }

    @Test
    void setName() {
        final String name = "notification-name";
        notification.setName(name);

        assertEquals(name, notification.getName());
    }

    @Test
    void setSchedule() {
        final String schedule = "* * * * *";
        notification.setSchedule(schedule);

        assertEquals(schedule, notification.getSchedule().get());
    }

    @Test
    void setType() {
        final String type = "slack/notification";
        notification.setType(type);

        assertEquals(type, notification.getType());
    }

    @Test
    void setConfig() {
        SlackNotification config = new SlackNotification();
        config.setChannel("test-channel");
        notification.setConfig(config);

        assertEquals("test-channel", notification.getConfig().getChannel());
    }

    @Test
    void testTimeZone() {
        TimeZone tz = TimeZone.getTimeZone(notification.getTimeZone());
        assertEquals("Coordinated Universal Time", tz.getDisplayName());

        notification.setTimeZone("EST");
        tz = TimeZone.getTimeZone(notification.getTimeZone());
        assertEquals("Eastern Standard Time", tz.getDisplayName());
    }

    @Test
    void setExtending() {
        final Extending extending = new Extending();
        extending.setName("name");
        extending.setRepository("repo");
        notification.setExtending(extending);

        assertEquals("name", notification.getExtending().get().getName());
        assertEquals("repo", notification.getExtending().get().getRepository());

    }
}