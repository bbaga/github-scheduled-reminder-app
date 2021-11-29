package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHPullRequest;
import org.mockito.Mockito;

import java.io.IOException;

class DraftFilterTest {

    @Test
    void testFilterPR() throws IOException {
        com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.DraftFilter config = new com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.DraftFilter();
        DraftFilter filter = new DraftFilter();
        filter.configure(config);

        GHPullRequest pr = Mockito.mock(GHPullRequest.class);
        Mockito.when(pr.isDraft()).thenReturn(true);
        Assertions.assertTrue(filter.filter(pr));

        Mockito.when(pr.isDraft()).thenReturn(false);
        Assertions.assertFalse(filter.filter(pr));

        config.setIncludeDrafts(true);
        Mockito.when(pr.isDraft()).thenReturn(false);
        Assertions.assertFalse(filter.filter(pr));
    }

    @Test
    void testFilterPRThrowIOException() throws IOException {
        com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.DraftFilter config = new com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.DraftFilter();
        DraftFilter filter = new DraftFilter();
        filter.configure(config);

        GHPullRequest pr = Mockito.mock(GHPullRequest.class);
        Mockito.when(pr.isDraft()).thenThrow(IOException.class);
        Assertions.assertFalse(filter.filter(pr));
    }

    @Test
    void testFilterIssue() {
        DraftFilter filter = new DraftFilter();
        GHIssue pr = Mockito.mock(GHIssue.class);
        Assertions.assertTrue(filter.filter(pr));
    }
}
