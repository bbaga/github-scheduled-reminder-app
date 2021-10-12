package com.bbaga.githubscheduledreminderapp.jobs;

import com.bbaga.githubscheduledreminderapp.repositories.GitHubInstallationRepository;
import org.kohsuke.github.GHAppInstallation;
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

    @Autowired
    private GitHub gitHub;

    @Autowired
    private GitHubInstallationRepository installationRepository;

    private final Logger logger = LoggerFactory.getLogger(GitHubInstallationScan.class);

    public void execute(JobExecutionContext context) throws JobExecutionException {

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

        Scheduler scheduler = context.getScheduler();
        JobDetail job;
        String jobIdentity;

        for (Long installationId : checkList.keySet()) {
            jobIdentity = String.format("%s-", GitHubInstallationRepositoryScan.class.getName());

            if (!checkList.get(installationId)) {
                installationRepository.remove(installationId);
                try {
                    scheduler.deleteJob(new JobKey(jobIdentity));
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            }

            try {
                if (scheduler.getJobDetail(new JobKey(jobIdentity)) == null) {
                    job = JobBuilder.newJob(GitHubInstallationRepositoryScan.class)
                            .withIdentity(jobIdentity)
                            .usingJobData("installationId", installationId)
                            .storeDurably(true)
                            .build();
                    scheduler.addJob(job, true);
                }
                context.getScheduler().triggerJob(new JobKey(jobIdentity));
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }

        logger.info("Installation Scanning is done");
    }
}
