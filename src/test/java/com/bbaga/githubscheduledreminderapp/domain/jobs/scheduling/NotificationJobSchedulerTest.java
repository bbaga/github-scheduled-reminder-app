package com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling;

import com.bbaga.githubscheduledreminderapp.domain.configuration.Notification;
import com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling.NotificationJobScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.quartz.*;

import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

class NotificationJobSchedulerTest {

    private Scheduler scheduler;

    @BeforeEach
    void setUp() {
        this.scheduler = Mockito.mock(Scheduler.class);
    }

    @Test
    void createSchedule() throws SchedulerException {
        Notification notification = new Notification();
        notification.setName("test-schedule");
        notification.setSchedule("* * * * * ?");
        notification.setTimeZone("Europe/Berlin");

        NotificationJobScheduler jobScheduler = new NotificationJobScheduler(this.scheduler);
        jobScheduler.createSchedule(notification);

        ArgumentCaptor<JobDetail> jobDetailCaptor = ArgumentCaptor.forClass(JobDetail.class);
        ArgumentCaptor<CronTrigger> triggerCaptor = ArgumentCaptor.forClass(CronTrigger.class);

        Mockito.verify(this.scheduler, Mockito.times(1)).scheduleJob(jobDetailCaptor.capture(), triggerCaptor.capture());

        JobDetail capturedJobDetail = jobDetailCaptor.getValue();
        CronTrigger capturedTrigger = triggerCaptor.getValue();

        assertEquals(notification.getName(), capturedJobDetail.getKey().getName());
        assertEquals(notification.getName(), capturedTrigger.getKey().getName());
        assertEquals(notification.getSchedule().get(), capturedTrigger.getCronExpression());
        assertEquals(TimeZone.getTimeZone(notification.getTimeZone()), capturedTrigger.getTimeZone());
    }

    @Test
    void updateSchedule() throws SchedulerException {
        Notification notification = new Notification();
        notification.setName("test-schedule");
        notification.setSchedule("* * * * * ?");
        notification.setTimeZone("Europe/Berlin");

        NotificationJobScheduler jobScheduler = new NotificationJobScheduler(this.scheduler);
        jobScheduler.updateSchedule(notification);

        ArgumentCaptor<TriggerKey> triggerKeyCaptor = ArgumentCaptor.forClass(TriggerKey.class);
        ArgumentCaptor<CronTrigger> triggerCaptor = ArgumentCaptor.forClass(CronTrigger.class);

        Mockito.verify(this.scheduler, Mockito.times(1)).rescheduleJob(triggerKeyCaptor.capture(), triggerCaptor.capture());

        TriggerKey capturedTriggerKey = triggerKeyCaptor.getValue();
        CronTrigger capturedTrigger = triggerCaptor.getValue();

        assertEquals(notification.getName(), capturedTriggerKey.getName());
        assertEquals(notification.getSchedule().get(), capturedTrigger.getCronExpression());
        assertEquals(TimeZone.getTimeZone(notification.getTimeZone()), capturedTrigger.getTimeZone());
    }
}
