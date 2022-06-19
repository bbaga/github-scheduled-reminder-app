package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.hubspot.slack.client.methods.params.search.SearchMessagesParams;
import org.springframework.context.ApplicationEvent;

public class SearchAndDeleteEvent extends ApplicationEvent {
    final private SearchMessagesParams searchMessagesParams;

    public SearchAndDeleteEvent(Object source, SearchMessagesParams searchMessagesParams) {
        super(source);
        this.searchMessagesParams = searchMessagesParams;
    }

    public SearchMessagesParams getSearchMessagesParams() {
        return searchMessagesParams;
    }


    public static SearchAndDeleteEvent create(Object source, SearchMessagesParams searchMessagesParams) {
        return new SearchAndDeleteEvent(source, searchMessagesParams);
    }
}
