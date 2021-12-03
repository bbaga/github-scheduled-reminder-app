package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.SearchPRsByReviewersSourceConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.SourceConfig;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.filters.FilterChain;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.search.SearchIssueBuilder;
import org.kohsuke.github.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchByReviewersPRsSource implements SearchAsSourceInterface <GHIssue> {
    private SearchPRsByReviewersSourceConfig source;

    @Override
    public void configure(SourceConfig sourceConfig) {
        this.source = (SearchPRsByReviewersSourceConfig) sourceConfig;
    }

    @Override
    public ArrayList<GHIssue> get(GHRepository repo, GitHub client) throws IOException {
        HashMap<Integer, GHIssue> issues = new HashMap<>();

        findIssues(client, repo, issues, source.getUsers(), "is:pr repo:%s review-requested:%s");
        findIssues(client, repo, issues, source.getTeams(), "is:pr repo:%s team-review-requested:%s");

        return new ArrayList<>(issues.values());
    }

    private void findIssues(
        GitHub client,
        GHRepository repo,
        HashMap<Integer, GHIssue> issues,
        List<String> searchSubjects,
        String queryTemplate
    ) {
        SearchIssueBuilder builder = SearchIssueBuilder.from(client.searchIssues());

        for (String subject : searchSubjects) {
            builder.query(String.format(queryTemplate, repo.getFullName(), subject))
                .forEach((GHIssue issue) -> this.processIssue(repo, issues, issue));
        }
    }

    private void processIssue(GHRepository repo, HashMap<Integer, GHIssue> issues, GHIssue issue) {
        int issueNumber = issue.getNumber();
        if (!issues.containsKey(issueNumber)) {
            try {
                GHPullRequest pullRequest = repo.getPullRequest(issueNumber);

                if (FilterChain.filter(source.getFilters(), pullRequest)) {
                    return;
                }

                issues.put(issueNumber, pullRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
