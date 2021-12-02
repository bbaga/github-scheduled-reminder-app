package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.Source;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchIssuesSource implements SearchAsSourceInterface <GHIssue> {
    private com.bbaga.githubscheduledreminderapp.domain.configuration.sources.SearchIssuesSource source;

    @Override
    public void configure(Source source) {
        this.source = (com.bbaga.githubscheduledreminderapp.domain.configuration.sources.SearchIssuesSource) source;
    }

    @Override
    public ArrayList<GHIssue> get(GHRepository repo, GitHub client) throws IOException {
        HashMap<Integer, GHIssue> issues = new HashMap<>();

        client.searchIssues().q(String.format("repo:%s %s", repo.getFullName(), source.getQuery())).list().forEach((GHIssue issue) -> {
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
