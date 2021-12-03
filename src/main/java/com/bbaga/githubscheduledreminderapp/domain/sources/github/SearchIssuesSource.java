package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.SearchIssuesSourceConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.SourceConfig;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.filters.FilterChain;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.search.SearchIssueBuilder;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchIssuesSource implements SearchAsSourceInterface <GHIssue> {
    private SearchIssuesSourceConfig source;

    @Override
    public void configure(SourceConfig sourceConfig) {
        this.source = (SearchIssuesSourceConfig) sourceConfig;
    }

    @Override
    public ArrayList<GHIssue> get(GHRepository repo, GitHub client) throws IOException {
        HashMap<Integer, GHIssue> issues = new HashMap<>();

        SearchIssueBuilder builder = SearchIssueBuilder.from(client.searchIssues());

        builder.query(String.format("repo:%s %s", repo.getFullName(), source.getQuery())).forEach((GHIssue issue) -> {
            int issueNumber = issue.getNumber();
            if (!issues.containsKey(issueNumber)) {
                try {
                    GHIssue properIssue;

                    if (issue.isPullRequest()) {
                        properIssue = repo.getPullRequest(issueNumber);
                    } else {
                        properIssue = repo.getIssue(issueNumber);
                    }

                    if (FilterChain.filter(source.getFilters(), properIssue)) {
                        return;
                    }

                    issues.put(issueNumber, properIssue);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return new ArrayList<>(issues.values());
    }
}
