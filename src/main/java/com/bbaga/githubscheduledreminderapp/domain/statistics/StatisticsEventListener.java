package com.bbaga.githubscheduledreminderapp.domain.statistics;

import org.springframework.context.ApplicationListener;

public class StatisticsEventListener implements ApplicationListener<StatisticsEvent> {

    private AggregatedStatisticsStorage statisticsStorage;

    public StatisticsEventListener(AggregatedStatisticsStorage statisticsStorage) {
        this.statisticsStorage = statisticsStorage;
    }

    @Override
    public void onApplicationEvent(StatisticsEvent event) {
        statisticsStorage.increase(event.getEventName());
    }
}
