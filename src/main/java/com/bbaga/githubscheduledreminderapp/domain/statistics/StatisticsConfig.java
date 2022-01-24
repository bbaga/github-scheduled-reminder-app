package com.bbaga.githubscheduledreminderapp.domain.statistics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatisticsConfig {

    @Value("${application.activityTracking.enabled}")
    private Boolean activityTrackingEnabled = false;

    @Value("${application.activityTracking.endpoint.url}")
    private String activityTrackingEndpointUrl = "";

    @Bean
    public AggregatedStatisticsStorage getStatisticsAggregator() {
        return new AggregatedStatisticsStorage();
    }

    @Bean
    public StatisticsEventListener getStatisticsEventListener(AggregatedStatisticsStorage statisticsStorage) {
        return new StatisticsEventListener(statisticsStorage);
    }

    @Bean
    public UrlBuilderInterface getTrackingUrlBuilder() {
        if (activityTrackingEnabled) {
            return new TrackingUrlBuilder(this.activityTrackingEndpointUrl);
        } else {
            return new PassThroughUrlBuilder();
        }
    }
}
