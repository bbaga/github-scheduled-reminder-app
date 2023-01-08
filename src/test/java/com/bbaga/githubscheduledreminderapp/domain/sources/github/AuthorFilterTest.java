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
        Mockito.when(pr.getUser()).thenReturn(mockIncludeUser);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //Included authors should not be filtered
        Assertions.assertFalse(filter.filter(pr));

        config.setIncludeAuthors(List.of());
        //Both Filters have nothing - no filtering
        Assertions.assertFalse(filter.filter(pr));
    }

    @Test
    void filter_exclude() throws IOException {
        AuthorFilterConfig config = new AuthorFilterConfig();
        config.setExcludeAuthors(List.of(EXCLUDE_USERNAME));
        AuthorFilter filter = new AuthorFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.getUser()).thenReturn(mockExcludeUser);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //Excluded authors should be filtered
        Assertions.assertTrue(filter.filter(pr));

        config.setExcludeAuthors(List.of());
        //Both Filters have nothing - no filtering
        Assertions.assertFalse(filter.filter(pr));
    }

    @Test
    void filter_include_with_expiraton() throws IOException {
        AuthorFilterConfig config = new AuthorFilterConfig();
        config.setIncludeAuthors(List.of(INCLUDE_USERNAME));
        config.setExpiryDays(100);
        AuthorFilter filter = new AuthorFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.getUser()).thenReturn(mockExcludeUser);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //non-Included authors should be filtered when expiration is not active
        Assertions.assertTrue(filter.filter(pr));
    }

    @Test
    void filter_include_with_no_expiraton() throws IOException {
        AuthorFilterConfig config = new AuthorFilterConfig();
        config.setIncludeAuthors(List.of(INCLUDE_USERNAME));
        config.setExpiryDays(-1);
        AuthorFilter filter = new AuthorFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.getUser()).thenReturn(mockExcludeUser);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //non-Included authors should not be filtered when expiration is not active
        Assertions.assertFalse(filter.filter(pr));
    }

    @Test
    void filter_exclude_with_expiration() throws IOException {
        AuthorFilterConfig config = new AuthorFilterConfig();
        config.setExcludeAuthors(List.of(EXCLUDE_USERNAME));
        config.setExpiryDays(-1);
        AuthorFilter filter = new AuthorFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.getUser()).thenReturn(mockExcludeUser);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //Excluded authors should not be filtered when expiration is active
        Assertions.assertFalse(filter.filter(pr));
    }

    @Test
    void filter_exclude_with_no_expiration() throws IOException {
        AuthorFilterConfig config = new AuthorFilterConfig();
        config.setExcludeAuthors(List.of(EXCLUDE_USERNAME));
        config.setExpiryDays(100);
        AuthorFilter filter = new AuthorFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.getUser()).thenReturn(mockExcludeUser);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //Excluded authors should be filtered when expiration is not active
        Assertions.assertTrue(filter.filter(pr));
    }

    @Test
    void filter_exclude_with_null_expiration() throws IOException {
        AuthorFilterConfig config = new AuthorFilterConfig();
        config.setExcludeAuthors(List.of(EXCLUDE_USERNAME));
        config.setExpiryDays(100);
        AuthorFilter filter = new AuthorFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.getUser()).thenReturn(mockExcludeUser);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //Excluded authors should be filtered when expiration is not active
        Assertions.assertTrue(filter.filter(pr));
    }

    @Test
    void filter_include_exclude() throws IOException {
        AuthorFilterConfig config = new AuthorFilterConfig();
        config.setIncludeAuthors(List.of(INCLUDE_USERNAME));
        config.setExcludeAuthors(List.of(EXCLUDE_USERNAME));
        AuthorFilter filter = new AuthorFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.getUser()).thenReturn(mockIncludeUser);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //Included user should not be filtered
        Assertions.assertFalse(filter.filter(pr));

        Mockito.when(pr.getUser()).thenReturn(mockExcludeUser);
        //Excluded user should be filtered
        Assertions.assertTrue(filter.filter(pr));

        Mockito.when(pr.getUser()).thenReturn(mockUnkownUser);
        //non-Included user should be filtered
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
        Mockito.when(pr.getUser()).thenReturn(mockIncludeUser);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //Included and Excluded User should not be filtered
        Assertions.assertFalse(filter.filter(pr));
    }
}
