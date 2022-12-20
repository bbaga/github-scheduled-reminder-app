package com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.util.stream.Collectors.toCollection;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LabelFilterConfig extends AbstractFilterConfig {

    public static final String LABEL_ACKNOWLEDGED = "acknowledged";

    @JsonProperty("include")
    private List<String> includeLabels;

    @JsonProperty("exclude")
    @JsonAlias("exclude-labels")
    private List<String> excludeLabels;

    @JsonProperty("expiry-days")
    private int expiryDays = Integer.MAX_VALUE;

    public LabelFilterConfig() {
        super(Filters.LABEL_FILTER.label);
        excludeLabels = new ArrayList<>(List.of(LABEL_ACKNOWLEDGED));
        includeLabels = new ArrayList<>();
    }

    public List<String> getIncludeLabels() { return  includeLabels; }

    public void setIncludeLabels(List<String> includeLabels) {
        if (includeLabels != null) {
            includeLabels = includeLabels.stream().map(
                (String label) -> label.toLowerCase(Locale.ROOT)).collect(toCollection(ArrayList::new)
            );
        }
        this.includeLabels = includeLabels;
    }

    public List<String> getExcludeLabels() {
        return excludeLabels;
    }

    public void setExcludeLabels(List<String> excludeLabels) {
        if (excludeLabels != null) {
            excludeLabels = excludeLabels.stream().map(
                (String label) -> label.toLowerCase(Locale.ROOT)).collect(toCollection(ArrayList::new)
            );
        }
        this.excludeLabels = excludeLabels;
    }

    public int getExpiryDays() {
        return expiryDays;
    }

    public void setExpiryDays(int expiryDays) {
        this.expiryDays = expiryDays;
    }
}
