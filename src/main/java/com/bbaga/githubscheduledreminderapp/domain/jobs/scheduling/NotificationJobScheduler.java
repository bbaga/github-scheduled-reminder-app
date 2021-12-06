package com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling;

import com.bbaga.githubscheduledreminderapp.domain.configuration.Notification;
import com.bbaga.githubscheduledreminderapp.application.jobs.ScheduledNotification;
import org.quartz.*;

import java.util.Optional;
import java.util.TimeZone;

public class NotificationJobScheduler {
    private final Scheduler scheduler;

    public NotificationJobScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void upsertSchedule(Notification notification) throws SchedulerException {
        Optional<String> schedule = notification.getSchedule();

        if (schedule.isEmpty()) {
            return;
        }

        JobKey jobKey = new JobKey(notification.getRepository());

        if (scheduler.checkExists(jobKey)) {
            updateSchedule(notification);
        } else {
            createSchedule(notification);
        }
    }

    public void createSchedule(Notification notification) throws SchedulerException {
        Optional<String> schedule = notification.getSchedule();

        if (schedule.isEmpty()) {
            return;
        }

        JobKey jobKey = new JobKey(notification.getRepository());

        JobDetail job = JobBuilder.newJob(ScheduledNotification.class)
            .withIdentity(jobKey)
            .storeDurably(true)
            .build();

        TimeZone timeZone = TimeZone.getTimeZone(notification.getTimeZone());

        Trigger trigger = TriggerBuilder.newTrigger()
            .withSchedule(CronScheduleBuilder.cronSchedule(schedule.get()).inTimeZone(timeZone))
            .withIdentity(jobKey.getName()).build();

        scheduler.scheduleJob(job, trigger);
    }

    public void updateSchedule(Notification notification) throws SchedulerException {
        Optional<String> schedule = notification.getSchedule();

        if (schedule.isEmpty()) {
            return;
        }

        TimeZone timeZone = TimeZone.getTimeZone(notification.getTimeZone());

        Trigger newTrigger = TriggerBuilder.newTrigger()
            .withSchedule(CronScheduleBuilder.cronSchedule(schedule.get()).inTimeZone(timeZone))
            .withIdentity(notification.getRepository()).build();
        scheduler.rescheduleJob(new TriggerKey(notification.getRepository()), newTrigger);
    }
}
