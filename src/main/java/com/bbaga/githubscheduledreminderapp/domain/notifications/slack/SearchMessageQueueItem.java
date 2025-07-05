package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.slack.api.methods.request.search.SearchMessagesRequest;
import com.slack.api.model.MatchedItem;
import java.util.Objects;
import java.util.function.Consumer;

public class SearchMessageQueueItem {
  protected SearchMessagesRequest params;
  protected Consumer<MatchedItem> action;

  public SearchMessageQueueItem(SearchMessagesRequest params, Consumer<MatchedItem> action) {
    this.params = params;
    this.action = action;
  }

  public SearchMessagesRequest getParams() {
    return params;
  }

  public Consumer<MatchedItem> getAction() {
    return action;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SearchMessageQueueItem)) {
      return false;
    }
    SearchMessageQueueItem that = (SearchMessageQueueItem) o;
    return getParams().equals(that.getParams()) && getAction().equals(that.getAction());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getParams(), getAction());
  }
}
