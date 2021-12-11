package com.bbaga.githubscheduledreminderapp.infrastructure.configuration.persitance;

import java.util.HashMap;

public class ConfigPersistenceGCSBucketSettings implements ConfigPersistenceSettingsInterface {
    private String filePath;
    private String credentialsJsonPath;
    private String credentialsJson;
    private String bucketName;

    public String getFilePath() {
        return filePath;
    }

    public String getCredentialsJsonPath() {
        return credentialsJsonPath;
    }

    public String getCredentialsJson() {
        return credentialsJson;
    }

    public String getBucketName() {
        return bucketName;
    }

    @Override
    public void load(HashMap<String, ?> settings) {
        filePath = settings.get("filePath").toString();
        bucketName = settings.get("bucketName").toString();

        if (settings.get("credentialsJsonPath") != null) {
            credentialsJsonPath = settings.get("credentialsJsonPath").toString();
        }

        if (settings.get("credentialsJson") != null) {
            credentialsJson = settings.get("credentialsJson").toString();
        }
    }
}
