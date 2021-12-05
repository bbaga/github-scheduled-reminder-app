package com.bbaga.githubscheduledreminderapp.application.controllers;

import com.bbaga.githubscheduledreminderapp.domain.statistics.AggregatedStatisticsStorage;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class Redirect {

    private final AggregatedStatisticsStorage statisticsStorage;

    Redirect(AggregatedStatisticsStorage statisticsStorage) {
        this.statisticsStorage = statisticsStorage;
    }

    @GetMapping("/redirect/action")
    public RedirectView redirect(@RequestParam String source, @RequestParam String action, @RequestParam String targetUrl) {
        statisticsStorage.increase(String.format("%s.%s", source, action));
        return new RedirectView(targetUrl);
    }
}
