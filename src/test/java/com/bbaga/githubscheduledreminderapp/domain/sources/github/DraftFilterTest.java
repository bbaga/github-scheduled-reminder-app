package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.DraftFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.filters.DraftFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHPullRequest;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Date;

class DraftFilterTest {

    @Test
    void testFilterPR() throws IOException {
        DraftFilterConfig config = new DraftFilterConfig();
        DraftFilter filter = new DraftFilter();
        filter.configure(config);

        GHPullRequest pr = Mockito.mock(GHPullRequest.class);
        Mockito.when(pr.isDraft()).thenReturn(true);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        Assertions.assertTrue(filter.filter(pr));

        Mockito.when(pr.isDraft()).thenReturn(false);
        Assertions.assertFalse(filter.filter(pr));

        config.setIncludeDrafts(true);
        Mockito.when(pr.isDraft()).thenReturn(false);
        Assertions.assertFalse(filter.filter(pr));
    }

    @Test
    void testFilterPRThrowIOException() throws IOException {
        DraftFilterConfig config = new DraftFilterConfig();
        DraftFilter filter = new DraftFilter();
        filter.configure(config);

        GHPullRequest pr = Mockito.mock(GHPullRequest.class);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        Mockito.when(pr.isDraft()).thenThrow(IOException.class);
        Assertions.assertFalse(filter.filter(pr));
    }

    @Test
    void testFilterIssue() throws IOException {
        DraftFilter filter = new DraftFilter();
        GHIssue pr = Mockito.mock(GHIssue.class);
        Mockito.when(pr.getUpdatedAt()).thenReturn(new Date());
        Assertions.assertFalse(filter.filter(pr));
    }
}
