package com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling;

import com.bbaga.githubscheduledreminderapp.application.jobs.ScheduledNotification;
import com.bbaga.githubscheduledreminderapp.domain.configuration.ScheduledNotificationConfigurationInterface;
import org.quartz.*;

import java.util.Optional;
import java.util.TimeZone;

public class NotificationJobScheduler {
    private final Scheduler scheduler;

    public NotificationJobScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void upsertSchedule(String fullName, ScheduledNotificationConfigurationInterface config) throws SchedulerException {
        Optional<String> schedule = config.getSchedule();

        if (schedule.isEmpty()) {
            return;
        }

        JobKey jobKey = new JobKey(fullName);

        if (scheduler.checkExists(jobKey)) {
            updateSchedule(fullName, config);
        } else {
            createSchedule(fullName, config);
        }
    }

    public void createSchedule(String fullName, ScheduledNotificationConfigurationInterface config) throws SchedulerException {
        Optional<String> schedule = config.getSchedule();

        if (schedule.isEmpty()) {
            return;
        }

        JobKey jobKey = new JobKey(fullName);

        JobDetail job = JobBuilder.newJob(ScheduledNotification.class)
            .withIdentity(jobKey)
            .storeDurably(true)
            .build();

        TimeZone timeZone = TimeZone.getTimeZone(config.getTimeZone());

        Trigger trigger = TriggerBuilder.newTrigger()
            .withSchedule(CronScheduleBuilder.cronSchedule(schedule.get()).inTimeZone(timeZone))
            .withIdentity(jobKey.getName()).build();

        scheduler.scheduleJob(job, trigger);
    }

    public void updateSchedule(String fullName, ScheduledNotificationConfigurationInterface config) throws SchedulerException {
        Optional<String> schedule = config.getSchedule();

        if (schedule.isEmpty()) {
            return;
        }

        TimeZone timeZone = TimeZone.getTimeZone(config.getTimeZone());

        Trigger newTrigger = TriggerBuilder.newTrigger()
            .withSchedule(CronScheduleBuilder.cronSchedule(schedule.get()).inTimeZone(timeZone))
            .withIdentity(fullName).build();
        scheduler.rescheduleJob(new TriggerKey(fullName), newTrigger);
    }
}
