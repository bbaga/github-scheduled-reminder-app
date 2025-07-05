package com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling;

import com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling.exceptions.SchedulingException;

import java.util.Map;

/**
 * Interface for job scheduling operations.
 * This interface abstracts the scheduling operations to decouple the domain layer from external scheduling libraries.
 */
public interface JobSchedulingService {
    
    /**
     * Checks if a job with the given key exists.
     *
     * @param jobKey The job key
     * @return True if the job exists, false otherwise
     * @throws SchedulingException If an error occurs during the check
     */
    boolean checkJobExists(String jobKey) throws SchedulingException;
    
    /**
     * Gets a job detail by its key.
     *
     * @param jobKey The job key
     * @return The job detail, or null if not found
     * @throws SchedulingException If an error occurs during the operation
     */
    Object getJobDetail(String jobKey) throws SchedulingException;
    
    /**
     * Schedules a job with the given trigger.
     *
     * @param jobClass The job class
     * @param jobKey The job key
     * @param triggerKey The trigger key
     * @param cronExpression The cron expression for the trigger
     * @param timeZoneId The time zone ID for the trigger
     * @param jobDataMap Additional job data
     * @throws SchedulingException If an error occurs during scheduling
     */
    void scheduleJob(Class<?> jobClass, String jobKey, String triggerKey, String cronExpression, String timeZoneId, Map<String, Object> jobDataMap) throws SchedulingException;
    
    /**
     * Reschedules a job with a new trigger.
     *
     * @param triggerKey The trigger key
     * @param cronExpression The cron expression for the new trigger
     * @param timeZoneId The time zone ID for the new trigger
     * @throws SchedulingException If an error occurs during rescheduling
     */
    void rescheduleJob(String triggerKey, String cronExpression, String timeZoneId) throws SchedulingException;
    
    /**
     * Adds a job to the scheduler.
     *
     * @param jobClass The job class
     * @param jobKey The job key
     * @param jobDataMap Additional job data
     * @param replace Whether to replace an existing job
     * @throws SchedulingException If an error occurs during the operation
     */
    void addJob(Class<?> jobClass, String jobKey, Map<String, Object> jobDataMap, boolean replace) throws SchedulingException;
    
    /**
     * Triggers a job immediately.
     *
     * @param jobKey The job key
     * @throws SchedulingException If an error occurs during the operation
     */
    void triggerJob(String jobKey) throws SchedulingException;
    
    /**
     * Deletes a job from the scheduler.
     *
     * @param jobKey The job key
     * @throws SchedulingException If an error occurs during the operation
     */
    void deleteJob(String jobKey) throws SchedulingException;
}