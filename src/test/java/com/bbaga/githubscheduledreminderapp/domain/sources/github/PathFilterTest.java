package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.PathFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.filters.PathFilter;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.*;

public class PathFilterTest {
    @Test
    void testFilterPR() throws IOException {
        PathFilterConfig config = new PathFilterConfig();
        PathFilter filter = new PathFilter();
        filter.configure(config);

        // No paths configured
        GitHubPullRequest pr = Mockito.mock(GitHubPullRequest.class);
        Mockito.when(pr.listFileNames()).thenReturn(Arrays.asList("src/foo/foo.java", "src/bar/bar.java"));
        Assertions.assertFalse(filter.filter(pr));

        // Exact matching path configured
        config.setIncludePaths(List.of("src/foo/foo.java"));
        Assertions.assertFalse(filter.filter(pr));

        // Partial matching path configured
        config.setIncludePaths(List.of("src/bar/"));
        Assertions.assertFalse(filter.filter(pr));

        // No matching path configured
        config.setIncludePaths(List.of("test/"));
        Assertions.assertTrue(filter.filter(pr));
    }

    @Test
    void testFilterPRThrowIOException() throws IOException {
        PathFilterConfig config = new PathFilterConfig();
        PathFilter filter = new PathFilter();
        filter.configure(config);

        GitHubPullRequest pr = Mockito.mock(GitHubPullRequest.class);
        Mockito.when(pr.listFileNames()).thenThrow(IOException.class);
        Assertions.assertFalse(filter.filter(pr));
    }

    @Test
    void testFilterIssue() throws IOException {
        PathFilter filter = new PathFilter();
        GitHubIssue issue = Mockito.mock(GitHubIssue.class);
        Assertions.assertFalse(filter.filter(issue));
    }
}
