package com.bbaga.githubscheduledreminderapp.domain.configuration;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.*;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilter;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.DraftFilter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;

import static com.bbaga.githubscheduledreminderapp.domain.configuration.sources.Sources.Constants.*;

public class NotificationConfiguration implements NotificationConfigurationInterface {

    @JsonSubTypes({
        @JsonSubTypes.Type(value = RepositoryIssuesSource.class, name = REPOSITORY_ISSUES),
        @JsonSubTypes.Type(value = RepositoryPRsSource.class, name = REPOSITORY_PRS),
        @JsonSubTypes.Type(value = SearchIssuesSource.class, name = SEARCH_ISSUES),
        @JsonSubTypes.Type(value = SearchPRsByReviewersSource.class, name = SEARCH_PRS_BY_REVIEWERS),
    })
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true,
        defaultImpl = RepositoryPRsSource.class
    )
    private ArrayList<Source> sources = new ArrayList<>();

    public NotificationConfiguration() {}

    @Override
    public ArrayList<Source> getSources() {
        return sources.size() == 0 ? NotificationConfiguration.getDefaultSources() : sources;
    }

    @Override
    public void setSources(ArrayList<Source> sources) {
        this.sources = sources;
    }

    public static ArrayList<Source> getDefaultSources() {
        ArrayList<Source> sources = new ArrayList<>();
        ArrayList<AbstractFilter> prFilters = new ArrayList<>();
        prFilters.add(new DraftFilter());

        sources.add(new RepositoryIssuesSource(REPOSITORY_ISSUES));
        sources.add(new RepositoryPRsSource(REPOSITORY_PRS, prFilters));

        return sources;
    }
}
