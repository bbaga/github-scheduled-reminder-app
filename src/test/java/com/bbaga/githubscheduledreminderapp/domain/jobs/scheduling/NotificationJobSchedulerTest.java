package com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling;

import com.bbaga.githubscheduledreminderapp.domain.configuration.Notification;
import com.bbaga.githubscheduledreminderapp.domain.configuration.SlackNotificationConfiguration;
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
        SlackNotificationConfiguration config = new SlackNotificationConfiguration();
        Notification notification = new Notification(config);
        notification.setRepository("test");
        notification.setName("schedule");
        notification.setSchedule("* * * * * ?");
        notification.setTimeZone("Europe/Berlin");

        NotificationJobScheduler jobScheduler = new NotificationJobScheduler(this.scheduler);
        jobScheduler.createSchedule(notification);

        ArgumentCaptor<JobDetail> jobDetailCaptor = ArgumentCaptor.forClass(JobDetail.class);
        ArgumentCaptor<CronTrigger> triggerCaptor = ArgumentCaptor.forClass(CronTrigger.class);

        Mockito.verify(this.scheduler, Mockito.times(1)).scheduleJob(jobDetailCaptor.capture(), triggerCaptor.capture());

        JobDetail capturedJobDetail = jobDetailCaptor.getValue();
        CronTrigger capturedTrigger = triggerCaptor.getValue();

        assertEquals(notification.getFullName(), capturedJobDetail.getKey().getName());
        assertEquals(notification.getFullName(), capturedTrigger.getKey().getName());
        assertEquals(notification.getSchedule().get(), capturedTrigger.getCronExpression());
        assertEquals(TimeZone.getTimeZone(notification.getTimeZone()), capturedTrigger.getTimeZone());
    }

    @Test
    void updateSchedule() throws SchedulerException {
        SlackNotificationConfiguration config = new SlackNotificationConfiguration();
        Notification notification = new Notification(config);
        notification.setRepository("test");
        notification.setName("schedule");
        notification.setSchedule("* * * * * ?");
        notification.setTimeZone("Europe/Berlin");

        NotificationJobScheduler jobScheduler = new NotificationJobScheduler(this.scheduler);
        jobScheduler.updateSchedule(notification);

        ArgumentCaptor<TriggerKey> triggerKeyCaptor = ArgumentCaptor.forClass(TriggerKey.class);
        ArgumentCaptor<CronTrigger> triggerCaptor = ArgumentCaptor.forClass(CronTrigger.class);

        Mockito.verify(this.scheduler, Mockito.times(1)).rescheduleJob(triggerKeyCaptor.capture(), triggerCaptor.capture());

        TriggerKey capturedTriggerKey = triggerKeyCaptor.getValue();
        CronTrigger capturedTrigger = triggerCaptor.getValue();

        assertEquals(notification.getFullName(), capturedTriggerKey.getName());
        assertEquals(notification.getSchedule().get(), capturedTrigger.getCronExpression());
        assertEquals(TimeZone.getTimeZone(notification.getTimeZone()), capturedTrigger.getTimeZone());
    }
}
