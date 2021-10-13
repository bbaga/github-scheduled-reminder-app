package com.bbaga.githubscheduledreminderapp.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

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

        assertEquals(schedule, notification.getSchedule());
    }

    @Test
    void setType() {
        final String type = "slack/notification";
        notification.setType(type);

        assertEquals(type, notification.getType());
    }

    @Test
    void setConfig() {
        final HashMap<String, String> map = new HashMap<>();
        map.put("foo", "bar");
        notification.setConfig(map);

        assertEquals("bar", notification.getConfig().get("foo"));
    }

    @Test
    void setExtending() {
        final Extending extending = new Extending();
        extending.setName("name");
        extending.setRepository("repo");
        notification.setExtending(extending);

        assertEquals("name", notification.getExtending().getName());
        assertEquals("repo", notification.getExtending().getRepository());

    }
}