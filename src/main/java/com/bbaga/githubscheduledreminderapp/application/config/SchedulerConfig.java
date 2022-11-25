package com.bbaga.githubscheduledreminderapp.application.config;

import com.bbaga.githubscheduledreminderapp.application.jobs.ScheduledStateDump;
import com.bbaga.githubscheduledreminderapp.application.jobs.SlackChannelMessageDelete;
import com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling.InstallationScanJobScheduler;
import com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling.NotificationJobScheduler;
import com.slack.api.methods.MethodsClient;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchedulerConfig {

  @Bean
  @Qualifier("Scheduler")
  public Scheduler schedulerBootstrap(
      Scheduler scheduler
  ) throws Exception {

    JobDetail job = JobBuilder.newJob(ScheduledStateDump.class)
        .withIdentity(ScheduledStateDump.class.getName())
        .storeDurably(true)
        .build();

    Trigger scheduleDump = TriggerBuilder.newTrigger()
        .withIdentity(ScheduledStateDump.class.getName())
        .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(1).repeatForever())
        .build();

    scheduler.scheduleJob(job, scheduleDump);

    JobDetail SlackChannelMessageDeleteJob = JobBuilder.newJob(SlackChannelMessageDelete.class)
        .withIdentity(SlackChannelMessageDelete.class.getName())
        .storeDurably(true)
        .build();

    Trigger SlackChannelMessageDeleteTrigger = TriggerBuilder.newTrigger()
        .withIdentity(SlackChannelMessageDelete.class.getName())
        .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(1).repeatForever())
        .build();

    try {
      scheduler.scheduleJob(SlackChannelMessageDeleteJob, SlackChannelMessageDeleteTrigger);
    } catch (SchedulerException e) {
      throw new RuntimeException(e);
    }

    return scheduler;
  }

  @Bean
  public NotificationJobScheduler getNotificationJobScheduler(@Qualifier("Scheduler") Scheduler scheduler) {
    return new NotificationJobScheduler(scheduler);
  }

  @Bean
  public InstallationScanJobScheduler getInstallationScanJobScheduler(@Qualifier("Scheduler") Scheduler scheduler) {
    return new InstallationScanJobScheduler(scheduler);
  }
}
