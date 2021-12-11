package com.bbaga.githubscheduledreminderapp.application;

import com.bbaga.githubscheduledreminderapp.infrastructure.configuration.persitance.ConfigPersistenceFactory;
import com.bbaga.githubscheduledreminderapp.infrastructure.configuration.persitance.ConfigPersistenceInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@ComponentScan("com.bbaga.githubscheduledreminderapp")
public class PersistentStorageConfig {

    @Value("${application.state.storage.type}")
    private String stateStorageType;

    @Value("${application.state.storage.fs.filepath}")
    private String stateStorageFsFilePath;

    @Value("${application.state.storage.gcs_bucket.name}")
    private String stateStorageGCSBucketName;

    @Value("${application.state.storage.gcs_bucket.secretFile}")
    private String stateStorageGCSBucketSecretFile;

    @Value("${application.state.storage.gcs_bucket.secret}")
    private String stateStorageGCSBucketSecret;

    @Value("${application.state.storage.gcs_bucket.filepath}")
    private String stateStorageGCSBucketFilePath;

    @Bean
    public ConfigPersistenceInterface getPersistentConfigStorage() {
        ConfigPersistenceFactory factory = new ConfigPersistenceFactory();
        HashMap<String, ?> config;
        ConfigPersistenceFactory.PersistenceType type;

        if (Objects.equals(this.stateStorageType, ConfigPersistenceFactory.PersistenceType.LOCAL_FS.label)) {
            type = ConfigPersistenceFactory.PersistenceType.LOCAL_FS;
            config = new HashMap<>(Map.of("filePath", stateStorageFsFilePath));
        } else if (Objects.equals(this.stateStorageType, ConfigPersistenceFactory.PersistenceType.GCS_BUCKET.label)) {
            type = ConfigPersistenceFactory.PersistenceType.GCS_BUCKET;
            config = new HashMap<>(Map.of(
                "bucketName", stateStorageGCSBucketName,
                "credentialsJson", stateStorageGCSBucketSecret,
                "credentialsJsonPath", stateStorageGCSBucketSecretFile,
                "filePath", stateStorageGCSBucketFilePath
            ));
        } else {
            throw new RuntimeException("Could not create state loader instance");
        }

        return factory.create(type, config);
    }
}
