package com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling;

import com.bbaga.githubscheduledreminderapp.application.jobs.GitHubInstallationRepositoryScan;
import org.quartz.*;

public class InstallationScanJobScheduler {
    private final Scheduler scheduler;

    public InstallationScanJobScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void triggerJob(long installationId) throws SchedulerException {

        String jobIdentity = getJobIdentity(installationId);

        if (scheduler.getJobDetail(new JobKey(jobIdentity)) == null) {
            JobDetail job = JobBuilder.newJob(GitHubInstallationRepositoryScan.class)
                    .withIdentity(jobIdentity)
                    .usingJobData("installationId", installationId)
                    .storeDurably(true)
                    .build();
            scheduler.addJob(job, true);
        }
        scheduler.triggerJob(new JobKey(jobIdentity));
    }

    public void deleteJob(long installationId) throws SchedulerException {
        String jobIdentity = getJobIdentity(installationId);
        scheduler.deleteJob(new JobKey(jobIdentity));
    }

    private String getJobIdentity(long installationId) {
        return String.format("%s-%s", GitHubInstallationRepositoryScan.class.getName(), installationId);
    }
}
