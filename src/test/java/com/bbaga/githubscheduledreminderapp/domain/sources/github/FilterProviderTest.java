package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.DraftFilterConfig;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.filters.DraftFilter;
import com.bbaga.githubscheduledreminderapp.domain.sources.github.filters.FilterProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class FilterProviderTest {

    @Test
    void get() {
        DraftFilterConfig config = new DraftFilterConfig();
        Assertions.assertTrue(
            FilterProvider.get(config) instanceof DraftFilter
        );
    }

    @Test
    void getUnknown() {
        AbstractFilterConfig config = Mockito.mock(AbstractFilterConfig.class);
        Mockito.when(config.getType()).thenReturn("FooBar");
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> FilterProvider.get(config));

        Assertions.assertEquals("Unknown filter type: \"FooBar\"", exception.getMessage());
    }
}