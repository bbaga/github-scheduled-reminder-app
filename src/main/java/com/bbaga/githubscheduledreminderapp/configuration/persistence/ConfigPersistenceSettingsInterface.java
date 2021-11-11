package com.bbaga.githubscheduledreminderapp.configuration.persistence;

import java.util.HashMap;

public interface ConfigPersistenceSettingsInterface {
    void load(HashMap<String, ?> settings);
}
