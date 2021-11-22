package com.bbaga.githubscheduledreminderapp.infrastructure.configuration.persitance;

import java.util.HashMap;

public interface ConfigPersistenceSettingsInterface {
    void load(HashMap<String, ?> settings);
}
