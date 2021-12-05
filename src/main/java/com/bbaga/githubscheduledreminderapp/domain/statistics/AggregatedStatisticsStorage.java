package com.bbaga.githubscheduledreminderapp.domain.statistics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class AggregatedStatisticsStorage {

    private final ConcurrentHashMap<String, AtomicLong> aggregates = new ConcurrentHashMap<>();

    public void increase(String name) {
        if (!aggregates.containsKey(name)) {
            aggregates.putIfAbsent(name, new AtomicLong(0));
        }

        aggregates.get(name).incrementAndGet();
    }

    public ConcurrentHashMap<String, AtomicLong> getAggregates() {
        synchronized (aggregates) {
            return aggregates;
        }
    }

    public void reset () {
        synchronized (aggregates) {
            aggregates.clear();
        }
    }
}
