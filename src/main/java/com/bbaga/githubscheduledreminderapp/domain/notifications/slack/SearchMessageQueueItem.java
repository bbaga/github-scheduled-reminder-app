package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.slack.api.methods.request.conversations.ConversationsHistoryRequest;
import com.slack.api.methods.request.search.SearchMessagesRequest;
import com.slack.api.model.MatchedItem;
import com.slack.api.model.Message;
import java.util.Objects;
import java.util.function.Consumer;

public class SearchMessageQueueItem {
  protected SearchRequest params;
  protected Consumer<Message> action;

  public SearchMessageQueueItem(SearchRequest params, Consumer<Message> action) {
    this.params = params;
    this.action = action;
  }

  public SearchRequest getParams() {
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
