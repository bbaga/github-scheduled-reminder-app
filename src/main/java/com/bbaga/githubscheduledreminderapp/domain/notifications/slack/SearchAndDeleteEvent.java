package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.slack.api.methods.request.search.SearchMessagesRequest;
import java.time.Instant;
import org.springframework.context.ApplicationEvent;

public class SearchAndDeleteEvent extends ApplicationEvent {
    final private SearchMessagesRequest searchMessagesParams;
    final private Instant deleteMessagesBefore;

    public SearchAndDeleteEvent(
        Object source,
        SearchMessagesRequest searchMessagesParams,
        Instant deleteMessagesBefore
    ) {
        super(source);
        this.searchMessagesParams = searchMessagesParams;
        this.deleteMessagesBefore = deleteMessagesBefore;
    }

    public SearchMessagesRequest getSearchMessagesParams() {
        return searchMessagesParams;
    }

    public Instant getDeleteMessagesBefore() {
        return deleteMessagesBefore;
    }

    public static SearchAndDeleteEvent create(
        Object source,
        SearchMessagesRequest searchMessagesParams,
        Instant deleteMessagesBefore
    ) {
        return new SearchAndDeleteEvent(source, searchMessagesParams, deleteMessagesBefore);
    }
}
