package com.bbaga.githubscheduledreminderapp.configuration.persistence;

import com.bbaga.githubscheduledreminderapp.configuration.ConfigGraphNode;

import java.util.concurrent.ConcurrentHashMap;

public interface ConfigPersistenceInterface {
    ConcurrentHashMap<String, ConfigGraphNode> load();
    void dump(ConcurrentHashMap<String, ConfigGraphNode> config);
}
