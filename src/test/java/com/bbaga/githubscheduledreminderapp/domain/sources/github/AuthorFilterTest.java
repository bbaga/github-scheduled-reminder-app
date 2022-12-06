package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import static org.mockito.Mockito.mock;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AuthorFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.filters.AuthorFilter;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubUser;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

class AuthorFilterTest {

    String INCLUDE_USERNAME = "include-user";

    String EXCLUDE_USERNAME = "exclude-user";

    String UNKNOWN_USERNAME = "unknown-user";

    @Mock
    GitHubUser mockIncludeUser;

    @Mock
    GitHubUser mockExcludeUser;

    @Mock
    GitHubUser mockUnkownUser;

    @BeforeEach
    void beforeEach() {
        mockIncludeUser = mock(GitHubUser.class);
        mockExcludeUser = mock(GitHubUser.class);
        mockUnkownUser = mock(GitHubUser.class);
        Mockito.when(mockIncludeUser.getLogin()).thenReturn(INCLUDE_USERNAME);
        Mockito.when(mockExcludeUser.getLogin()).thenReturn(EXCLUDE_USERNAME);
        Mockito.when(mockUnkownUser.getLogin()).thenReturn(UNKNOWN_USERNAME);
    }

    @Test
    void filter_include() throws IOException {
        AuthorFilterConfig config = new AuthorFilterConfig();
        config.setIncludeAuthors(List.of(INCLUDE_USERNAME));
        AuthorFilter filter = new AuthorFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.isDraft()).thenReturn(false);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        Mockito.when(pr.getUser()).thenReturn(mockIncludeUser);
        Assertions.assertFalse(filter.filter(pr));

        config.setIncludeAuthors(List.of());
        Assertions.assertTrue(filter.filter(pr));
    }

    @Test
    void filter_exclude() throws IOException {
        AuthorFilterConfig config = new AuthorFilterConfig();
        config.setExcludeAuthors(List.of(EXCLUDE_USERNAME));
        AuthorFilter filter = new AuthorFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.isDraft()).thenReturn(false);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        Mockito.when(pr.getUser()).thenReturn(mockExcludeUser);
        Assertions.assertTrue(filter.filter(pr));

        config.setExcludeAuthors(List.of());
        Assertions.assertFalse(filter.filter(pr));
    }

    @Test
    void filter_include_exclude() throws IOException {
        AuthorFilterConfig config = new AuthorFilterConfig();
        config.setIncludeAuthors(List.of(INCLUDE_USERNAME));
        config.setExcludeAuthors(List.of(EXCLUDE_USERNAME));
        AuthorFilter filter = new AuthorFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.isDraft()).thenReturn(true);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        Mockito.when(pr.getUser()).thenReturn(mockIncludeUser);
        Assertions.assertFalse(filter.filter(pr));

        Mockito.when(pr.getUser()).thenReturn(mockExcludeUser);
        Assertions.assertTrue(filter.filter(pr));
    }

    @Test
    void filter_include_exclude_same_user() throws IOException {
        AuthorFilterConfig config = new AuthorFilterConfig();
        config.setIncludeAuthors(List.of(INCLUDE_USERNAME));
        config.setExcludeAuthors(List.of(INCLUDE_USERNAME));
        AuthorFilter filter = new AuthorFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.isDraft()).thenReturn(true);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        Mockito.when(pr.getUser()).thenReturn(mockIncludeUser);
        Assertions.assertFalse(filter.filter(pr));
    }

    @Test
    void filter_unknownUser_Include() throws IOException {
        AuthorFilterConfig config = new AuthorFilterConfig();
        config.setIncludeAuthors(List.of(INCLUDE_USERNAME));
        AuthorFilter filter = new AuthorFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.isDraft()).thenReturn(true);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        Mockito.when(pr.getUser()).thenReturn(mockUnkownUser);
        Assertions.assertTrue(filter.filter(pr));
    }

    @Test
    void filter_unknownUser_Exclude() throws IOException {
        AuthorFilterConfig config = new AuthorFilterConfig();
        config.setExcludeAuthors(List.of(EXCLUDE_USERNAME));
        AuthorFilter filter = new AuthorFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.isDraft()).thenReturn(true);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        Mockito.when(pr.getUser()).thenReturn(mockUnkownUser);
        Assertions.assertFalse(filter.filter(pr));
    }
}
