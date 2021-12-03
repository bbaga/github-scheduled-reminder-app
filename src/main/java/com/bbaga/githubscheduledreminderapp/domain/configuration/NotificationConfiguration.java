package com.bbaga.githubscheduledreminderapp.domain.configuration;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.*;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.DraftFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.LabelFilterConfig;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.List;

import static com.bbaga.githubscheduledreminderapp.domain.configuration.sources.Sources.Constants.*;

public class NotificationConfiguration implements NotificationConfigurationInterface {

    @JsonSubTypes({
        @JsonSubTypes.Type(value = RepositoryIssuesSourceConfig.class, name = REPOSITORY_ISSUES),
        @JsonSubTypes.Type(value = RepositoryPRsSourceConfig.class, name = REPOSITORY_PRS),
        @JsonSubTypes.Type(value = SearchIssuesSourceConfig.class, name = SEARCH_ISSUES),
        @JsonSubTypes.Type(value = SearchPRsByReviewersSourceConfig.class, name = SEARCH_PRS_BY_REVIEWERS),
    })
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true,
        defaultImpl = RepositoryPRsSourceConfig.class
    )
    private ArrayList<SourceConfig> sources = new ArrayList<>();

    public NotificationConfiguration() {}

    @Override
    public ArrayList<SourceConfig> getSources() {
        return sources.size() == 0 ? NotificationConfiguration.getDefaultSources() : sources;
    }

    @Override
    public void setSources(ArrayList<SourceConfig> sourceConfigs) {
        this.sources = sourceConfigs;
    }

    public static ArrayList<SourceConfig> getDefaultSources() {
        ArrayList<SourceConfig> sourceConfigs = new ArrayList<>();
        ArrayList<AbstractFilterConfig> prFilters = new ArrayList<>();
        prFilters.add(new DraftFilterConfig());

        LabelFilterConfig labelFilterConfig = new LabelFilterConfig();

        ArrayList<AbstractFilterConfig> issueFilters = new ArrayList<>();
        issueFilters.add(labelFilterConfig);

        sourceConfigs.add(new RepositoryIssuesSourceConfig(REPOSITORY_ISSUES, issueFilters));
        sourceConfigs.add(new RepositoryPRsSourceConfig(REPOSITORY_PRS, prFilters));

        return sourceConfigs;
    }
}
