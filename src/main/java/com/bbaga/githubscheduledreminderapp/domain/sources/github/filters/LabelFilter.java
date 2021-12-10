package com.bbaga.githubscheduledreminderapp.domain.sources.github.filters;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.LabelFilterConfig;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;
import org.kohsuke.github.GHLabel;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LabelFilter implements IssueFilterInterface {
    private LabelFilterConfig config;

    @Override
    public void configure(AbstractFilterConfig config) {
        this.config = (LabelFilterConfig) config;
    }

    @Override
    public Boolean filter(GitHubIssue issue) {
        List<String> excludedLabels = config.getExcludeLabels();
        Instant now = Instant.now();
        long ageInDays = 0;

        try {
            Date date = issue.getUpdatedAt();

            if (date == null) {
                date = issue.getCreatedAt();
            }

            ageInDays = ChronoUnit.DAYS.between(date.toInstant(), now);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (GHLabel label : issue.getLabels()) {
            if (excludedLabels.contains(label.getName().toLowerCase(Locale.ROOT)) && ageInDays < config.getExpiryDays()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Boolean filter(GitHubPullRequest issue) {
        return false;
    }
}
