package com.bbaga.githubscheduledreminderapp.domain.sources.github.filters;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.DraftFilterConfig;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHPullRequest;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DraftFilter implements IssueFilterInterface {
    private DraftFilterConfig config;

    @Override
    public void configure(AbstractFilterConfig config) {
        this.config = (DraftFilterConfig) config;
    }

    @Override
    public Boolean filter(GHIssue issue) {
        return false;
    }

    @Override
    public Boolean filter(GHPullRequest pr) {
        Instant now = Instant.now();
        long ageInDays = 2;

        try {
            Date date = pr.getUpdatedAt();

            if (date == null) {
                date = pr.getCreatedAt();
            }

            ageInDays = ChronoUnit.DAYS.between(date.toInstant(), now);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return !config.getIncludeDrafts() && pr.isDraft() && ageInDays < config.getExpiryDays();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
