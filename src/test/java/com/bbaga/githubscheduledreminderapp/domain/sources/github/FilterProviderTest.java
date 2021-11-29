package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.AbstractFilter;
import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.filters.DraftFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class FilterProviderTest {

    @Test
    void get() {
        DraftFilter config = new DraftFilter();
        Assertions.assertTrue(
            FilterProvider.get(config) instanceof com.bbaga.githubscheduledreminderapp.domain.sources.github.DraftFilter
        );
    }

    @Test
    void getUnknown() {
        AbstractFilter config = Mockito.mock(AbstractFilter.class);
        Mockito.when(config.getType()).thenReturn("FooBar");
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> FilterProvider.get(config));

        Assertions.assertEquals("Unknown filter type: \"FooBar\"", exception.getMessage());
    }
}