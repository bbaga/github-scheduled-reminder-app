package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import org.springframework.context.ApplicationEvent;

public class SearchAndDeleteEvent extends ApplicationEvent {
    final private SearchRequest searchRequest;

    public SearchAndDeleteEvent(
        Object source,
        SearchRequest searchRequest
    ) {
        super(source);
        this.searchRequest = searchRequest;
    }

    public SearchRequest getSearchRequest() {
        return searchRequest;
    }

    public static SearchAndDeleteEvent create(
        Object source,
        SearchRequest searchMessagesParams
    ) {
        return new SearchAndDeleteEvent(source, searchMessagesParams);
    }
}
