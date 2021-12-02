package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.SearchPRsByReviewersSource;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.Source;
import org.kohsuke.github.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchByReviewersPRsSource implements SearchAsSourceInterface <GHIssue> {
    private SearchPRsByReviewersSource source;

    @Override
    public void configure(Source source) {
        this.source = (SearchPRsByReviewersSource) source;
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
        for (String subject : searchSubjects) {
            client.searchIssues().q(String.format(queryTemplate, repo.getFullName(), subject))
                .list()
                .forEach((GHIssue issue) -> this.processIssue(repo, issues, issue));
        }
    }

    private void processIssue(GHRepository repo, HashMap<Integer, GHIssue> issues, GHIssue issue) {
        int issueNumber = issue.getNumber();
        if (!issues.containsKey(issueNumber)) {
            try {
                GHIssue properIssue = repo.getPullRequest(issueNumber);

                if (FilterChain.filter(source.getFilters(), properIssue)) {
                    return;
                }

                issues.put(issueNumber, properIssue);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
