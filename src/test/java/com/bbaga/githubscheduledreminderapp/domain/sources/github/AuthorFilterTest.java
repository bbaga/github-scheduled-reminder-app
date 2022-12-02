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

    String TEST_USER = "test-user";

    @Mock
    GitHubUser mockUser;

    @BeforeEach
    void beforeEach() {
        mockUser = mock(GitHubUser.class);
        Mockito.when(mockUser.getLogin()).thenReturn(TEST_USER);
    }

    @Test
    void testFilterPR() throws IOException {
        AuthorFilterConfig config = new AuthorFilterConfig();
        config.setIncludeAuthors(List.of(TEST_USER));
        AuthorFilter filter = new AuthorFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.isDraft()).thenReturn(true);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        Mockito.when(pr.getUser()).thenReturn(mockUser);
        Assertions.assertFalse(filter.filter(pr));

        config.setIncludeAuthors(List.of());
        Assertions.assertTrue(filter.filter(pr));
    }
}
