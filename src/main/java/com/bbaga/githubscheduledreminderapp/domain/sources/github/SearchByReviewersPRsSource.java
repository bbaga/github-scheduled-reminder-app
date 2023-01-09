package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.SearchPRsByReviewersSourceConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.SourceConfig;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.filters.FilterChain;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.search.SearchIssueBuilder;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;
import org.kohsuke.github.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchByReviewersPRsSource implements SearchAsSourceInterface <GitHubIssue> {
    private SearchPRsByReviewersSourceConfig source;

    @Override
    public void configure(SourceConfig sourceConfig) {
        this.source = (SearchPRsByReviewersSourceConfig) sourceConfig;
    }

    @Override
    public List<GitHubIssue> get(GHRepository repo, GitHub client) throws IOException {
        HashMap<Integer, GitHubIssue> issues = new HashMap<>();

        findIssues(client, repo, issues, source.getUsers(), "is:pr is:open repo:%s review-requested:%s");
        findIssues(client, repo, issues, source.getTeams(), "is:pr is:open repo:%s team-review-requested:%s");

        return new ArrayList<>(issues.values());
    }

    private void findIssues(
        GitHub client,
        GHRepository repo,
        HashMap<Integer, GitHubIssue> issues,
        List<String> searchSubjects,
        String queryTemplate
    ) {
        for (String subject : searchSubjects) {
            SearchIssueBuilder builder = SearchIssueBuilder.from(client.searchIssues());
            builder.query(String.format(queryTemplate, repo.getFullName(), subject))
                .forEach((GHIssue issue) -> this.processIssue(repo, issues, issue));
        }
    }

    private void processIssue(GHRepository repo, HashMap<Integer, GitHubIssue> issues, GHIssue issue) {
        int issueNumber = issue.getNumber();
        if (!issues.containsKey(issueNumber)) {
            try {
                GitHubPullRequest pullRequest = GitHubPullRequest.create(repo.getPullRequest(issueNumber));

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
