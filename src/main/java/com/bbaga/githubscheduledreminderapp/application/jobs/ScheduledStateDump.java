package com.bbaga.githubscheduledreminderapp.application.jobs;

import com.bbaga.githubscheduledreminderapp.infrastructure.configuration.persitance.ConfigPersistenceFactory;
import com.bbaga.githubscheduledreminderapp.infrastructure.configuration.persitance.ConfigPersistenceInterface;
import com.bbaga.githubscheduledreminderapp.domain.configuration.ConfigGraphNode;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@DisallowConcurrentExecution
public class ScheduledStateDump implements Job {

    @Autowired
    @Qualifier("ConfigGraph")
    private ConcurrentHashMap<String, ConfigGraphNode> configGraph;

    @Autowired
    @Qualifier("application.state.storage.fs.filepath")
    private String stateFilePath;

    private final Logger logger = LoggerFactory.getLogger(ScheduledStateDump.class);

    public void execute(JobExecutionContext context) {
        logger.info("Starting state dump job");

        ConfigPersistenceFactory factory = new ConfigPersistenceFactory();
        ConfigPersistenceInterface configPersistence = factory.create(
            ConfigPersistenceFactory.PersistenceType.LOCAL_FS,
            new HashMap<>(Map.of("filePath", stateFilePath))
        );

        configPersistence.dump(this.configGraph);

        logger.info("Finished state dump job");
    }
}
