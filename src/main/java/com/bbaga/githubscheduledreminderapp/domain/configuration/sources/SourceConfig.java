package com.bbaga.githubscheduledreminderapp.domain.configuration.sources;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AuthorFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.DraftFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.LabelFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.PathFilterConfig;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;

import static com.bbaga.githubscheduledreminderapp.domain.configuration.sources.Sources.Constants.*;
import static com.bbaga.githubscheduledreminderapp.domain.configuration.sources.Sources.Constants.SEARCH_PRS_BY_REVIEWERS;
import static com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.Filters.Constants.*;

@JsonSubTypes({
        @JsonSubTypes.Type(value = RepositoryIssuesSourceConfig.class, name = REPOSITORY_ISSUES),
        @JsonSubTypes.Type(value = RepositoryPRsSourceConfig.class, name = REPOSITORY_PRS),
        @JsonSubTypes.Type(value = SearchIssuesSourceConfig.class, name = SEARCH_ISSUES),
        @JsonSubTypes.Type(value = SearchPRsByReviewersSourceConfig.class, name = SEARCH_PRS_BY_REVIEWERS),
        @JsonSubTypes.Type(value = EventPrReviewRequestedAsSourceConfig.class, name = EVENT_PR_REVIEW_REQUESTED),
})
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true,
        defaultImpl = RepositoryPRsSourceConfig.class
)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class SourceConfig {
    protected String type;

    @JsonSubTypes({
            @JsonSubTypes.Type(value = DraftFilterConfig.class, name = DRAFT_FILTER),
            @JsonSubTypes.Type(value = LabelFilterConfig.class, name = LABEL_FILTER),
            @JsonSubTypes.Type(value = AuthorFilterConfig.class, name = AUTHOR_FILTER),
            @JsonSubTypes.Type(value = PathFilterConfig.class, name = PATH_FILTER),
    })
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
    protected ArrayList<AbstractFilterConfig> filters;

    public SourceConfig(String type, ArrayList<AbstractFilterConfig> filters) {
        this.type = type;
        this.filters = filters;
    }

    public SourceConfig() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<AbstractFilterConfig> getFilters() {
        return filters;
    }

    public void setFilters(ArrayList<AbstractFilterConfig> filters) {
        this.filters = filters;
    }

    public Boolean hasRepositoryAsSource() {
        return false;
    }

    public Boolean hasSearchAsSource() {
        return false;
    }

    public Boolean hasEventAsSource() {
        return false;
    }
}
