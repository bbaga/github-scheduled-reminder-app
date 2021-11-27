package com.bbaga.githubscheduledreminderapp.domain.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    private Notification notification;

    @BeforeEach
    void setUp() {
        notification = new Notification();
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

        assertEquals("test-channel", ((SlackNotification) notification.getConfig()).getChannel());
    }

    @Test
    void testTimeZone() {
        TimeZone tz = TimeZone.getTimeZone(notification.getTimeZone());
        assertEquals("Coordinated Universal Time", tz.getDisplayName());

        notification.setTimeZone("EST");
        tz = TimeZone.getTimeZone(notification.getTimeZone());
        assertEquals("Eastern Standard Time", tz.getDisplayName());
    }
}