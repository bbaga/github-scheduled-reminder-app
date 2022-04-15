package com.bbaga.githubscheduledreminderapp.application.jobs;

import com.bbaga.githubscheduledreminderapp.domain.jobs.scheduling.InstallationScanJobScheduler;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.repositories.GitHubInstallationRepository;
import org.kohsuke.github.GHAppInstallation;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

@Component
@DisallowConcurrentExecution
public class GitHubInstallationScan implements Job {

    private final GitHub gitHub;
    private final GitHubInstallationRepository installationRepository;
    private InstallationScanJobScheduler jobScheduler;
    private final Logger logger = LoggerFactory.getLogger(GitHubInstallationScan.class);

    @Autowired
    public GitHubInstallationScan(
            GitHub gitHub,
            GitHubInstallationRepository installationRepository,
            InstallationScanJobScheduler jobScheduler
    ) {
        this.gitHub = gitHub;
        this.installationRepository = installationRepository;
        this.jobScheduler = jobScheduler;
    }

    public void execute(JobExecutionContext context) {

        logger.info("Starting Installation Scan");
        PagedIterable<GHAppInstallation> list = null;
        try {
            list = gitHub.getApp().listInstallations();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Set<Long> installationIds = installationRepository.getIds();
        HashMap<Long, Boolean> checkList = new HashMap<>();
        for (Long installationId : installationIds) {
            checkList.put(installationId, false);
        }

        assert list != null;
        for (GHAppInstallation installation : list) {
            checkList.put(installation.getId(), true);
            installationRepository.put(installation);
        }

        for (Long installationId : checkList.keySet()) {
            if (!checkList.get(installationId)) {
                installationRepository.remove(installationId);
                try {
                    jobScheduler.deleteJob(installationId);
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
                continue;
            }

            try {
                jobScheduler.triggerJob(installationId);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }

        logger.info("Installation Scanning is done");
    }
}
