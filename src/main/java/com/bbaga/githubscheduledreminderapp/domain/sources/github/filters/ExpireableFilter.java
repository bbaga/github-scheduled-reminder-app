package com.bbaga.githubscheduledreminderapp.domain.sources.github.filters;

import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class ExpireableFilter {

  public boolean isFilterExpired(GitHubIssue issue, int expiryDays) {
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

    return ageInDays > expiryDays;
  }

  public boolean isFilterExpired(GitHubPullRequest pullRequest, int expiryDays) {
    return this.isFilterExpired((GitHubIssue) pullRequest, expiryDays);
  }

}
