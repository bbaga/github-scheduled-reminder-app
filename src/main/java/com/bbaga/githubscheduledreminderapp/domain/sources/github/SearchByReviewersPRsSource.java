package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.SearchPRsByReviewersSource;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.Source;
import org.kohsuke.github.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchByReviewersPRsSource implements SearchAsSourceInterface <GHIssue> {
    private SearchPRsByReviewersSource source;

    @Override
    public void configure(Source source) {
        this.source = (SearchPRsByReviewersSource) source;
    }

    @Override
    public ArrayList<GHIssue> get(GHRepository repo, GitHub client) throws IOException {
        HashMap<Integer, GHIssue> issues = new HashMap<>();

        for (String user : source.getUsers()) {
            client.searchIssues().q(String.format("repo:%s review-requested:%s", repo.getFullName(), user)).list().forEach((GHIssue issue) -> {
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
            });
        }

        return new ArrayList<>(issues.values());
    }
}
