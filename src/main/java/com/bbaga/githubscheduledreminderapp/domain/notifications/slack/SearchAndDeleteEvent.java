package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.hubspot.slack.client.methods.params.search.SearchMessagesParams;
import java.time.Instant;
import org.springframework.context.ApplicationEvent;

public class SearchAndDeleteEvent extends ApplicationEvent {
    final private SearchMessagesParams searchMessagesParams;
    final private Instant deleteMessagesBefore;

    public SearchAndDeleteEvent(
        Object source,
        SearchMessagesParams searchMessagesParams,
        Instant deleteMessagesBefore
    ) {
        super(source);
        this.searchMessagesParams = searchMessagesParams;
        this.deleteMessagesBefore = deleteMessagesBefore;
    }

    public SearchMessagesParams getSearchMessagesParams() {
        return searchMessagesParams;
    }

    public Instant getDeleteMessagesBefore() {
        return deleteMessagesBefore;
    }

    public static SearchAndDeleteEvent create(
        Object source,
        SearchMessagesParams searchMessagesParams,
        Instant deleteMessagesBefore
    ) {
        return new SearchAndDeleteEvent(source, searchMessagesParams, deleteMessagesBefore);
    }
}
