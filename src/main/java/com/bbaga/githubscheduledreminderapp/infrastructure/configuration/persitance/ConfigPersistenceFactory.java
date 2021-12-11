package com.bbaga.githubscheduledreminderapp.infrastructure.configuration.persitance;

import java.util.HashMap;

public class ConfigPersistenceFactory {
    public enum PersistenceType {
        LOCAL_FS("LOCAL_FS"),
        GCS_BUCKET("GCS_BUCKET");

        public final String label;

        PersistenceType(String label) {
            this.label = label;
        }
    }

    public ConfigPersistenceInterface create(PersistenceType type, HashMap<String, ?> map) {
        ConfigPersistenceSettingsInterface settings;

        switch (type) {
            case LOCAL_FS:
                settings = new ConfigPersistenceLocalFileSettings();
                settings.load(map);
                return new ConfigPersistenceLocalFile((ConfigPersistenceLocalFileSettings) settings);
            case GCS_BUCKET:
                settings = new ConfigPersistenceGCSBucketSettings();
                settings.load(map);
                return new ConfigPersistenceGCSBucket((ConfigPersistenceGCSBucketSettings) settings);
            default:
                throw new RuntimeException("Persistence type is not implemented");
        }
    }
}
