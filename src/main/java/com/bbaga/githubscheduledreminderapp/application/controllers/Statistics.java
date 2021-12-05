package com.bbaga.githubscheduledreminderapp.application.controllers;

import com.bbaga.githubscheduledreminderapp.domain.statistics.AggregatedStatisticsStorage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class Statistics {

    private final AggregatedStatisticsStorage statisticsStorage;

    Statistics(AggregatedStatisticsStorage statisticsStorage) {
        this.statisticsStorage = statisticsStorage;
    }

    @GetMapping("/statistics/fetch")
    public HashMap<String, AtomicLong> fetch() {
        HashMap<String, AtomicLong> aggregates = new HashMap<>(statisticsStorage.getAggregates());
        statisticsStorage.reset();
        return aggregates;
    }

    @GetMapping("/statistics/show")
    public ConcurrentHashMap<String, AtomicLong> show() {
        return statisticsStorage.getAggregates();
    }
}
