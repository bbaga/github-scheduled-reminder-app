package com.bbaga.githubscheduledreminderapp.domain.sources.github.filters;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.LabelFilterConfig;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;
import java.util.stream.Collectors;
import org.kohsuke.github.GHLabel;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LabelFilter extends ExpireableFilter implements IssueFilterInterface {
    private LabelFilterConfig config;

    @Override
    public void configure(AbstractFilterConfig config) {
        this.config = (LabelFilterConfig) config;
    }

    @Override
    public Boolean filter(GitHubIssue issue) {

        //No matter what if the filter is expired it should not filter results.
        if (isFilterExpired(issue, config.getExpiryDays())) {
            return false;
        }

        List<String> includedLabels = config.getIncludeLabels();
        List<String> excludedLabels = config.getExcludeLabels();

        //Guard case - if both filters have nothing, filter nothing
        if ((includedLabels == null || includedLabels.isEmpty()) &&
            (excludedLabels == null || excludedLabels.isEmpty())) {
            return false;
        }

        List<String> issueLabels =
            issue.getLabels().stream().map(label -> label.getName().toLowerCase(Locale.ROOT)).toList();

        if (includedLabels != null && !includedLabels.isEmpty()) {
            boolean result = true;
            for (String filterLabel : includedLabels) {
                if (issueLabels.contains(filterLabel)) {
                    result = false;
                    break;
                }
            }
            return result;
        }

        if (excludedLabels != null && !excludedLabels.isEmpty()) {
            boolean result = false;
            for (String filterLabel : excludedLabels) {
                if (issueLabels.contains(filterLabel)) {
                    result = true;
                    break;
                }
            }
            return result;
        }

        return false;
    }

    @Override
    public Boolean filter(GitHubPullRequest issue) {
        return this.filter((GitHubIssue) issue);
    }
}
