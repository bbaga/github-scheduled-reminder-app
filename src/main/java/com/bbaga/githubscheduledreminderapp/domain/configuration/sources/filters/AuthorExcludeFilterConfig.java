package com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorExcludeFilterConfig extends AbstractFilterConfig {
  @JsonProperty("exclude")
  private List<String> excludeAuthors;

  public AuthorExcludeFilterConfig() {
    super(Filters.AUTHOR_EXCLUDE_FILTER.label);
    excludeAuthors = new ArrayList<>();
  }

  public List<String> getExcludeAuthors() {
    return excludeAuthors;
  }

  public void setExcludeAuthors(List<String> excludeAuthors) {
    this.excludeAuthors = excludeAuthors;
  }

}
