package com.bbaga.githubscheduledreminderapp.application.jobs.scheduling;

import com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling.JobSchedulingService;
import com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling.exceptions.SchedulingException;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.TimeZone;

/**
 * Implementation of JobSchedulingService using Quartz.
 * This class bridges the domain layer's scheduling abstractions with the Quartz library.
 */
@Service
public class QuartzJobSchedulingService implements JobSchedulingService {

    private final Scheduler scheduler;

    public QuartzJobSchedulingService(@org.springframework.beans.factory.annotation.Qualifier("Scheduler") Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public boolean checkJobExists(String jobKey) throws SchedulingException {
        try {
            return scheduler.checkExists(new JobKey(jobKey));
        } catch (SchedulerException e) {
            throw new SchedulingException("Error checking if job exists: " + jobKey, e);
        }
    }

    @Override
    public Object getJobDetail(String jobKey) throws SchedulingException {
        try {
            return scheduler.getJobDetail(new JobKey(jobKey));
        } catch (SchedulerException e) {
            throw new SchedulingException("Error getting job detail: " + jobKey, e);
        }
    }

    @Override
    public void scheduleJob(Class<?> jobClass, String jobKey, String triggerKey, String cronExpression, String timeZoneId, Map<String, Object> jobDataMap) throws SchedulingException {
        try {
            JobDetail job = JobBuilder.newJob((Class<? extends Job>) jobClass)
                .withIdentity(jobKey)
                .storeDurably(true)
                .build();

            if (jobDataMap != null && !jobDataMap.isEmpty()) {
                job.getJobDataMap().putAll(jobDataMap);
            }

            TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);

            Trigger trigger = TriggerBuilder.newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression).inTimeZone(timeZone))
                .withIdentity(triggerKey)
                .build();

            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            throw new SchedulingException("Error scheduling job: " + jobKey, e);
        }
    }

    @Override
    public void rescheduleJob(String triggerKey, String cronExpression, String timeZoneId) throws SchedulingException {
        try {
            TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);

            Trigger newTrigger = TriggerBuilder.newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression).inTimeZone(timeZone))
                .withIdentity(triggerKey)
                .build();

            scheduler.rescheduleJob(new TriggerKey(triggerKey), newTrigger);
        } catch (SchedulerException e) {
            throw new SchedulingException("Error rescheduling job with trigger: " + triggerKey, e);
        }
    }

    @Override
    public void addJob(Class<?> jobClass, String jobKey, Map<String, Object> jobDataMap, boolean replace) throws SchedulingException {
        try {
            JobDetail job = JobBuilder.newJob((Class<? extends Job>) jobClass)
                .withIdentity(jobKey)
                .storeDurably(true)
                .build();

            if (jobDataMap != null && !jobDataMap.isEmpty()) {
                job.getJobDataMap().putAll(jobDataMap);
            }

            scheduler.addJob(job, replace);
        } catch (SchedulerException e) {
            throw new SchedulingException("Error adding job: " + jobKey, e);
        }
    }

    @Override
    public void triggerJob(String jobKey) throws SchedulingException {
        try {
            scheduler.triggerJob(new JobKey(jobKey));
        } catch (SchedulerException e) {
            throw new SchedulingException("Error triggering job: " + jobKey, e);
        }
    }

    @Override
    public void deleteJob(String jobKey) throws SchedulingException {
        try {
            scheduler.deleteJob(new JobKey(jobKey));
        } catch (SchedulerException e) {
            throw new SchedulingException("Error deleting job: " + jobKey, e);
        }
    }
}
