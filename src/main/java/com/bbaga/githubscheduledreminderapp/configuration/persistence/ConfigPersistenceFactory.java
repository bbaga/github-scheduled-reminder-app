package com.bbaga.githubscheduledreminderapp.configuration.persistence;

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
        switch (type) {
            case LOCAL_FS:
                ConfigPersistenceLocalFileSettings settings = new ConfigPersistenceLocalFileSettings();
                settings.load(map);
                return new ConfigPersistenceLocalFile(settings.getFilePath());
            case GCS_BUCKET:
                throw new RuntimeException("Persistence type GCS_BUCKET is not implemented");
            default:
                throw new RuntimeException("Persistence type is not implemented");
        }
    }
}
