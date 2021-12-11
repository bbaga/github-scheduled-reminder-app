package com.bbaga.githubscheduledreminderapp.infrastructure.configuration.persitance;

import com.bbaga.githubscheduledreminderapp.domain.configuration.ConfigGraphNode;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.concurrent.ConcurrentHashMap;

public interface ConfigPersistenceInterface {
    ConcurrentHashMap<String, ConfigGraphNode> load();
    void dump(ConcurrentHashMap<String, ConfigGraphNode> config);
}
