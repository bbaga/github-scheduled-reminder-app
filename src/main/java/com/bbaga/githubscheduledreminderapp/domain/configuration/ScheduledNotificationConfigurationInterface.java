package com.bbaga.githubscheduledreminderapp.domain.configuration;

import java.util.Optional;

public interface ScheduledNotificationConfigurationInterface {
    Optional<String> getSchedule();

    void setSchedule(String schedule);

    String getTimeZone();

    void setTimeZone(String timeZone);
}
