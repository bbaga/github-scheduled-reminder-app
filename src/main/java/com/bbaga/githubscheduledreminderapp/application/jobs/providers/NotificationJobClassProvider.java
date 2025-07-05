package com.bbaga.githubscheduledreminderapp.application.jobs.providers;

import com.bbaga.githubscheduledreminderapp.application.jobs.ScheduledNotification;
import com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling.JobClassProvider;
import org.quartz.Job;
import org.springframework.stereotype.Component;

/**
 * Implementation of JobClassProvider that provides the ScheduledNotification job class.
 */
@Component("notificationJobClassProvider")
public class NotificationJobClassProvider implements JobClassProvider {
    @Override
    public Class<? extends Job> getJobClass() {
        return ScheduledNotification.class;
    }
}