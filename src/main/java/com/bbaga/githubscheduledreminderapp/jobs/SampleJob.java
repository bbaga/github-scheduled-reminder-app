package com.bbaga.githubscheduledreminderapp.jobs;

import org.quartz.*;
import org.springframework.stereotype.Component;

@Component
@PersistJobDataAfterExecution
public class SampleJob implements Job {

    public void execute(JobExecutionContext context) throws JobExecutionException {

        long executions;
        JobDataMap map = context.getJobDetail().getJobDataMap();

        if (map.containsKey("executions")) {
            executions = context.getJobDetail().getJobDataMap().getLong("executions");
        } else {
            executions = 1;
        }

        System.out.format("Job thing run #%d%n", executions);
        map.put("executions", ++executions);
    }
}
