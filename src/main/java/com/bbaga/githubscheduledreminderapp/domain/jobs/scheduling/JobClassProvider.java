package com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling;

import org.quartz.Job;

/**
 * Interface for providing job classes to job schedulers.
 * This interface helps to decouple the domain layer from the application layer
 * by removing direct dependencies on concrete job implementations.
 */
public interface JobClassProvider {
    /**
     * Returns the job class to be used for scheduling.
     *
     * @return The job class
     */
    Class<? extends Job> getJobClass();
}
