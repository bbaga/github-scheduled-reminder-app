package com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling.exceptions;

/**
 * Domain-specific exception for job scheduling operations.
 * This exception is used to decouple the domain layer from external scheduling libraries.
 */
public class SchedulingException extends Exception {
    
    public SchedulingException(String message) {
        super(message);
    }
    
    public SchedulingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public SchedulingException(Throwable cause) {
        super(cause);
    }
}