package com.bbaga.githubscheduledreminderapp.domain.configuration.sources;

import static com.bbaga.githubscheduledreminderapp.domain.configuration.sources.Sources.Constants.EVENT_PR_REVIEW_REQUESTED;
import static com.bbaga.githubscheduledreminderapp.domain.configuration.sources.Sources.Constants.REPOSITORY_ISSUES;
import static com.bbaga.githubscheduledreminderapp.domain.configuration.sources.Sources.Constants.REPOSITORY_PRS;
import static com.bbaga.githubscheduledreminderapp.domain.configuration.sources.Sources.Constants.SEARCH_ISSUES;
import static com.bbaga.githubscheduledreminderapp.domain.configuration.sources.Sources.Constants.SEARCH_PRS_BY_REVIEWERS;
import static com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.Filters.Constants.AUTHOR_FILTER;
import static com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.Filters.Constants.DRAFT_FILTER;
import static com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.Filters.Constants.LABEL_FILTER;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AuthorFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.DraftFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.LabelFilterConfig;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.ArrayList;

@JsonSubTypes({
        @JsonSubTypes.Type(value = SearchIssuesSourceConfig.class, name = SEARCH_ISSUES)
})
public abstract class SourceConfigMix {
}
