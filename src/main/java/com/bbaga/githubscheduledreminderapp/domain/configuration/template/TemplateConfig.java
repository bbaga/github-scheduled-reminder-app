package com.bbaga.githubscheduledreminderapp.domain.configuration.template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TemplateConfig {

    public static final String MODE_BLOCK  = "block";
    public static final String MODE_FREE_TEXT  = "free-text";

    @JsonProperty("header-main")
    private String headerMain = null;

    @JsonProperty("header-prs")
    private String headerPRs = null;

    @JsonProperty("header-issues")
    private String headerIssues = null;

    @JsonProperty("line-prs")
    private String linePRs = null;

    @JsonProperty("line-issues")
    private String lineIssues = null;

    @JsonProperty("no-results")
    private String noResults = null;

    @JsonProperty("skip-no-results")
    private Boolean skipNoResults = null;

    @JsonProperty("overflow-format")
    private String overflowFormat = null;

    private String mode = null;

    public String getHeaderMain() {
        return headerMain;
    }

    public void setHeaderMain(String headerMain) {
        this.headerMain = headerMain;
    }

    public String getHeaderPRs() {
        return headerPRs;
    }

    public void setHeaderPRs(String headerPRs) {
        this.headerPRs = headerPRs;
    }

    public String getHeaderIssues() {
        return headerIssues;
    }

    public void setHeaderIssues(String headerIssues) {
        this.headerIssues = headerIssues;
    }

    public String getLinePRs() {
        return linePRs;
    }

    public void setLinePRs(String linePRs) {
        this.linePRs = linePRs;
    }

    public String getLineIssues() {
        return lineIssues;
    }

    public void setLineIssues(String lineIssues) {
        this.lineIssues = lineIssues;
    }

    public String getNoResults() {
        return noResults;
    }

    public void setNoResults(String noResults) {
        this.noResults = noResults;
    }

    public Boolean getSkipNoResults() {
        return skipNoResults;
    }

    public void setSkipNoResults(Boolean skipNoResults) {
        this.skipNoResults = skipNoResults;
    }

    public String getOverflowFormat() {
        return overflowFormat;
    }

    public void setOverflowFormat(String overflowFormat) {
        this.overflowFormat = overflowFormat;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
