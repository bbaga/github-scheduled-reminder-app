package com.bbaga.githubscheduledreminderapp.application.jobs.providers;

import com.bbaga.githubscheduledreminderapp.application.jobs.GitHubInstallationRepositoryScan;
import com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling.JobClassProvider;
import org.quartz.Job;
import org.springframework.stereotype.Component;

/**
 * Implementation of JobClassProvider that provides the GitHubInstallationRepositoryScan job class.
 */
@Component("installationScanJobClassProvider")
public class InstallationScanJobClassProvider implements JobClassProvider {
    @Override
    public Class<? extends Job> getJobClass() {
        return GitHubInstallationRepositoryScan.class;
    }
}