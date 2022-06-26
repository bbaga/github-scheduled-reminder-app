package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.SearchIssuesSourceConfig;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.search.SearchIssueBuilder;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

class SearchIssuesSourceTest {

    @Test
    void get() throws IOException {

        GHIssue issueMock = Mockito.mock(GHIssue.class);
        Mockito.when(issueMock.isPullRequest()).thenReturn(false);
        Mockito.when(issueMock.getNumber()).thenReturn(1);
        GitHubIssue gitHubIssueMock = Mockito.mock(GitHubIssue.class);
        Mockito.when(gitHubIssueMock.unwrap()).thenReturn(issueMock);

        ArrayList<GHIssue> resultIssues = new ArrayList<>();
        resultIssues.add(issueMock);


        MockedStatic<GitHubIssue> gitHubIssue = Mockito.mockStatic(GitHubIssue.class);
        gitHubIssue.when(() -> GitHubIssue.create(issueMock)).thenReturn(gitHubIssueMock);


        GHRepository repository = Mockito.mock(GHRepository.class);
        Mockito.when(repository.getIssue(1)).thenReturn(issueMock);

        GHIssueSearchBuilder searchBuilder = Mockito.mock(GHIssueSearchBuilder.class);

        GitHub client = Mockito.mock(GitHub.class);
        Mockito.when(client.searchIssues()).thenReturn(searchBuilder);

        SearchIssueBuilder searchIssueBuilderMock = Mockito.mock(SearchIssueBuilder.class);

        MockedStatic<SearchIssueBuilder> searchIssueBuilder = Mockito.mockStatic(SearchIssueBuilder.class);
        searchIssueBuilder.when(() -> SearchIssueBuilder.from(searchBuilder)).thenReturn(searchIssueBuilderMock);

        SearchIssuesSourceConfig config = new SearchIssuesSourceConfig();
        config.setQuery("is:pr");

        SearchIssuesSource source = new SearchIssuesSource();
        source.configure(config);

        Mockito.when(repository.getFullName()).thenReturn("some/repo");

        Mockito.when(searchIssueBuilderMock.query("repo:some/repo is:pr")).thenReturn(resultIssues);

        List<GitHubIssue> issues = source.get(repository, client);

        assertSame(issueMock, issues.get(0).unwrap());
    }
}
