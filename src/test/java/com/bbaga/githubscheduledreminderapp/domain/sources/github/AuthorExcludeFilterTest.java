package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import static org.mockito.Mockito.mock;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AuthorExcludeFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.filters.AuthorExcludeFilter;
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

class AuthorExcludeFilterTest {

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
        AuthorExcludeFilterConfig config = new AuthorExcludeFilterConfig();
        config.setExcludeAuthors(List.of(TEST_USER));
        AuthorExcludeFilter filter = new AuthorExcludeFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.isDraft()).thenReturn(true);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        Mockito.when(pr.getUser()).thenReturn(mockUser);
        Assertions.assertTrue(filter.filter(pr));

        config.setExcludeAuthors(List.of());
        Assertions.assertFalse(filter.filter(pr));
    }
}
