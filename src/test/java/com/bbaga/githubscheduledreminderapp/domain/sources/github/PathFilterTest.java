package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import static org.mockito.Mockito.mock;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.PathFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.filters.PathFilter;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PathFilterTest {

    @Test
    void testFilterPR_include() throws IOException {
        PathFilterConfig config = new PathFilterConfig();
        PathFilter filter = new PathFilter();
        filter.configure(config);

        // No paths configured
        GitHubPullRequest pr = Mockito.mock(GitHubPullRequest.class);
        Mockito.when(pr.getFilenames()).thenReturn(Arrays.asList("src/foo/foo.java", "src/bar/bar.java"));
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
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
    void testFilterPR_exclude() throws IOException {
        PathFilterConfig config = new PathFilterConfig();
        PathFilter filter = new PathFilter();
        filter.configure(config);

        // No paths configured
        GitHubPullRequest pr = Mockito.mock(GitHubPullRequest.class);
        Mockito.when(pr.getFilenames()).thenReturn(Arrays.asList("src/foo/foo.java", "src/bar/bar.java"));
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        Assertions.assertFalse(filter.filter(pr));

        // Exact matching path configured
        config.setExcludePaths(List.of("src/foo/foo.java"));
        Assertions.assertTrue(filter.filter(pr));

        // Partial matching path configured
        config.setExcludePaths(List.of("src/bar/"));
        Assertions.assertTrue(filter.filter(pr));

        // No matching path configured
        config.setExcludePaths(List.of("test/"));
        Assertions.assertFalse(filter.filter(pr));
    }

    @Test
    void testFilterPR_exclude_expiration() throws IOException {
        PathFilterConfig config = new PathFilterConfig();
        PathFilter filter = new PathFilter();
        filter.configure(config);

        // No paths configured
        GitHubPullRequest pr = Mockito.mock(GitHubPullRequest.class);
        Mockito.when(pr.getFilenames()).thenReturn(Arrays.asList("src/foo/foo.java", "src/bar/bar.java"));
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
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
        Mockito.when(pr.getFilenames()).thenThrow(IOException.class);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        Assertions.assertFalse(filter.filter(pr));
    }

    @Test
    void testFilterIssue() throws IOException {
        PathFilter filter = new PathFilter();
        GitHubIssue issue = Mockito.mock(GitHubIssue.class);
        Assertions.assertFalse(filter.filter(issue));
    }




    String INCLUDE_PATH = "include-path";

    String EXCLUDE_PATH = "exclude-path";

    String UNKNOWN_PATH = "unknown-path";


    @Test
    void filter_include() throws IOException {
        PathFilterConfig config = new PathFilterConfig();
        config.setIncludePaths(List.of(INCLUDE_PATH));
        PathFilter filter = new PathFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.getFilenames()).thenReturn(List.of(INCLUDE_PATH));
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //Included path should not be filtered
        Assertions.assertFalse(filter.filter(pr));

        config.setIncludePaths(List.of());
        //Both Filters have nothing - no filtering
        Assertions.assertFalse(filter.filter(pr));
    }

    @Test
    void filter_exclude() throws IOException {
        PathFilterConfig config = new PathFilterConfig();
        config.setExcludePaths(List.of(EXCLUDE_PATH));
        PathFilter filter = new PathFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.getFilenames()).thenReturn(List.of(EXCLUDE_PATH));
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //Excluded path should be filtered
        Assertions.assertTrue(filter.filter(pr));

        config.setExcludePaths(List.of());
        //Both Filters have nothing - no filtering
        Assertions.assertFalse(filter.filter(pr));
    }

    @Test
    void filter_include_with_expiration() throws IOException {
        PathFilterConfig config = new PathFilterConfig();
        config.setIncludePaths(List.of(INCLUDE_PATH));
        config.setExpiryDays(100);
        PathFilter filter = new PathFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.getFilenames()).thenReturn(List.of());
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //non-Included path should be filtered when expiration is not active
        Assertions.assertTrue(filter.filter(pr));
    }

    @Test
    void filter_include_with_no_expiration() throws IOException {
        PathFilterConfig config = new PathFilterConfig();
        config.setIncludePaths(List.of(INCLUDE_PATH));
        config.setExpiryDays(-1);
        PathFilter filter = new PathFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.getFilenames()).thenReturn(List.of());
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //non-Included path should not be filtered when expiration is active
        Assertions.assertFalse(filter.filter(pr));
    }

    @Test
    void filter_exclude_with_expiration() throws IOException {
        PathFilterConfig config = new PathFilterConfig();
        config.setExcludePaths(List.of(EXCLUDE_PATH));
        config.setExpiryDays(100);
        PathFilter filter = new PathFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.getFilenames()).thenReturn(List.of(INCLUDE_PATH, EXCLUDE_PATH, UNKNOWN_PATH));
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //Excluded path should be filtered when expiration is not active
        Assertions.assertTrue(filter.filter(pr));
    }

    @Test
    void filter_exclude_with_no_expiration() throws IOException {
        PathFilterConfig config = new PathFilterConfig();
        config.setExcludePaths(List.of(EXCLUDE_PATH));
        config.setExpiryDays(-1);
        PathFilter filter = new PathFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.getFilenames()).thenReturn(List.of(INCLUDE_PATH, EXCLUDE_PATH, UNKNOWN_PATH));
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //Excluded path should not be filtered when expiration is not active
        Assertions.assertFalse(filter.filter(pr));
    }

    @Test
    void filter_include_exclude() throws IOException {
        PathFilterConfig config = new PathFilterConfig();
        config.setIncludePaths(List.of(INCLUDE_PATH));
        config.setExcludePaths(List.of(EXCLUDE_PATH));
        PathFilter filter = new PathFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.getFilenames()).thenReturn(List.of(INCLUDE_PATH));
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //Included path should not be filtered
        Assertions.assertFalse(filter.filter(pr));

        Mockito.when(pr.getFilenames()).thenReturn(List.of(EXCLUDE_PATH));
        //Excluded path should be filtered
        Assertions.assertTrue(filter.filter(pr));

        Mockito.when(pr.getFilenames()).thenReturn(List.of(UNKNOWN_PATH));
        //non-Included path should be filtered
        Assertions.assertTrue(filter.filter(pr));
    }

    @Test
    void filter_include_exclude_same_user() throws IOException {
        PathFilterConfig config = new PathFilterConfig();
        config.setIncludePaths(List.of(INCLUDE_PATH));
        config.setExcludePaths(List.of(INCLUDE_PATH));
        PathFilter filter = new PathFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.getFilenames()).thenReturn(List.of(INCLUDE_PATH));
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //Included and Excluded Path should not be filtered
        Assertions.assertFalse(filter.filter(pr));
    }
}
