package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import static org.mockito.Mockito.mock;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.LabelFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.filters.LabelFilter;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHLabel;
import org.mockito.Mock;
import org.mockito.Mockito;

class LabelFilterTest {

    String INCLUDE_LABEL = "include-label";

    String EXCLUDE_LABEL = "exclude-label";

    String UNKNOWN_LABEL = "unknown-label";


    List<GHLabel> mockLabels;

    List<GHLabel> mockEmptyLabels;

    @Mock
    GHLabel mockIncludeLabel;

    @Mock
    GHLabel mockExcludeLabel;

    @Mock
    GHLabel mockUnkownLabel;

    @BeforeEach
    void beforeEach() {
        mockIncludeLabel = mock(GHLabel.class);
        mockExcludeLabel = mock(GHLabel.class);
        mockUnkownLabel = mock(GHLabel.class);
        Mockito.when(mockIncludeLabel.getName()).thenReturn(INCLUDE_LABEL);
        Mockito.when(mockExcludeLabel.getName()).thenReturn(EXCLUDE_LABEL);
        Mockito.when(mockUnkownLabel.getName()).thenReturn(UNKNOWN_LABEL);
        mockLabels = new ArrayList<>();
        mockLabels.add(mockIncludeLabel);
        mockLabels.add(mockExcludeLabel);
        mockLabels.add(mockUnkownLabel);

        mockEmptyLabels = new ArrayList<>();
    }

    @Test
    void filter_include() throws IOException {
        LabelFilterConfig config = new LabelFilterConfig();
        config.setIncludeLabels(List.of(INCLUDE_LABEL));
        LabelFilter filter = new LabelFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.getLabels()).thenReturn(List.of(mockIncludeLabel));
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //Included label should not be filtered
        Assertions.assertFalse(filter.filter(pr));

        config.setIncludeLabels(List.of());
        //Both Filters have nothing - no filtering
        Assertions.assertFalse(filter.filter(pr));
    }

    @Test
    void filter_exclude() throws IOException {
        LabelFilterConfig config = new LabelFilterConfig();
        config.setExcludeLabels(List.of(EXCLUDE_LABEL));
        LabelFilter filter = new LabelFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.getLabels()).thenReturn(List.of(mockExcludeLabel));
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //Excluded label should be filtered
        Assertions.assertTrue(filter.filter(pr));

        config.setExcludeLabels(List.of());
        //Both Filters have nothing - no filtering
        Assertions.assertFalse(filter.filter(pr));
    }

    @Test
    void filter_include_with_expiration() throws IOException {
        LabelFilterConfig config = new LabelFilterConfig();
        config.setIncludeLabels(List.of(INCLUDE_LABEL));
        config.setExpiryDays(100);
        LabelFilter filter = new LabelFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.getLabels()).thenReturn(mockEmptyLabels);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //non-Included label should be filtered when expiration is not active
        Assertions.assertTrue(filter.filter(pr));
    }

    @Test
    void filter_include_with_no_expiration() throws IOException {
        LabelFilterConfig config = new LabelFilterConfig();
        config.setIncludeLabels(List.of(INCLUDE_LABEL));
        config.setExpiryDays(-1);
        LabelFilter filter = new LabelFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.getLabels()).thenReturn(mockEmptyLabels);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //non-Included label should not be filtered when expiration is active
        Assertions.assertFalse(filter.filter(pr));
    }

    @Test
    void filter_exclude_with_expiration() throws IOException {
        LabelFilterConfig config = new LabelFilterConfig();
        config.setExcludeLabels(List.of(EXCLUDE_LABEL));
        config.setExpiryDays(100);
        LabelFilter filter = new LabelFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.getLabels()).thenReturn(mockLabels);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //Excluded label should be filtered when expiration is not active
        Assertions.assertTrue(filter.filter(pr));
    }

    @Test
    void filter_exclude_with_no_expiration() throws IOException {
        LabelFilterConfig config = new LabelFilterConfig();
        config.setExcludeLabels(List.of(EXCLUDE_LABEL));
        config.setExpiryDays(-1);
        LabelFilter filter = new LabelFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.getLabels()).thenReturn(mockLabels);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //Excluded label should not be filtered when expiration is not active
        Assertions.assertFalse(filter.filter(pr));
    }

    @Test
    void filter_include_exclude() throws IOException {
        LabelFilterConfig config = new LabelFilterConfig();
        config.setIncludeLabels(List.of(INCLUDE_LABEL));
        config.setExcludeLabels(List.of(EXCLUDE_LABEL));
        LabelFilter filter = new LabelFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.getLabels()).thenReturn(List.of(mockIncludeLabel));
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //Included label should not be filtered
        Assertions.assertFalse(filter.filter(pr));

        Mockito.when(pr.getLabels()).thenReturn(List.of(mockExcludeLabel));
        //Excluded label should be filtered
        Assertions.assertTrue(filter.filter(pr));

        Mockito.when(pr.getLabels()).thenReturn(List.of(mockUnkownLabel));
        //non-Included label should be filtered
        Assertions.assertTrue(filter.filter(pr));
    }

    @Test
    void filter_include_exclude_same_user() throws IOException {
        LabelFilterConfig config = new LabelFilterConfig();
        config.setIncludeLabels(List.of(INCLUDE_LABEL));
        config.setExcludeLabels(List.of(INCLUDE_LABEL));
        LabelFilter filter = new LabelFilter();
        filter.configure(config);

        GitHubPullRequest pr = mock(GitHubPullRequest.class);
        Mockito.when(pr.getLabels()).thenReturn(List.of(mockIncludeLabel));
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        //Included and Excluded Label should not be filtered
        Assertions.assertFalse(filter.filter(pr));
    }
}
