package com.bbaga.githubscheduledreminderapp.domain.sources.github.filters;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AuthorExcludeFilterConfig;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;
import java.io.IOException;
import java.util.List;

public class AuthorExcludeFilter implements IssueFilterInterface {
  private AuthorExcludeFilterConfig config;

  @Override
  public void configure(AbstractFilterConfig config) {
    this.config = (AuthorExcludeFilterConfig) config;
  }

  @Override
  public Boolean filter(GitHubIssue issue) {
    List<String> excludeAuthors = config.getExcludeAuthors();

    try {
      return excludeAuthors == null || excludeAuthors.contains(issue.getUser().getLogin());
    } catch (IOException ignore) {}

    return false;
  }

  @Override
  public Boolean filter(GitHubPullRequest issue) {
    return this.filter((GitHubIssue) issue);
  }
}
