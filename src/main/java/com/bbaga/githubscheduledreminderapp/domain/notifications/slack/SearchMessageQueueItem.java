package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.hubspot.slack.client.methods.params.search.SearchMessagesParams;
import com.hubspot.slack.client.models.Message;
import java.util.Objects;
import java.util.function.Consumer;

public class SearchMessageQueueItem {
  protected SearchMessagesParams params;
  protected Consumer<Message> action;

  public SearchMessageQueueItem(SearchMessagesParams params, Consumer<Message> action) {
    this.params = params;
    this.action = action;
  }

  public SearchMessagesParams getParams() {
    return params;
  }

  public Consumer<Message> getAction() {
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
