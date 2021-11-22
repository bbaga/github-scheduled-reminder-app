package com.bbaga.githubscheduledreminderapp.infrastructure.configuration.persitance;

import java.util.HashMap;

public class ConfigPersistenceLocalFileSettings implements ConfigPersistenceSettingsInterface {
    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    @Override
    public void load(HashMap<String, ?> settings) {
        this.filePath = settings.get("filePath").toString();
    }
}
