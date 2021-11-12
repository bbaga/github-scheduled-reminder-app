package com.bbaga.githubscheduledreminderapp.repositories;

import com.bbaga.githubscheduledreminderapp.infrastructure.GitHub.AppInstallationContainer;
import com.bbaga.githubscheduledreminderapp.notifications.slack.ChannelNotificationDataProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.GHAppInstallation;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GitHubInstallationRepositoryTest {

    private GitHubInstallationRepository repository;

    @BeforeEach
    void setUp () {
        this.repository = new GitHubInstallationRepository();
    }

    @Test
    void remove() {
        long id = 123456L;
        GHAppInstallation installation = Mockito.mock(GHAppInstallation.class);
        AppInstallationContainer installationContainer = Mockito.mock(AppInstallationContainer.class);

        MockedStatic<AppInstallationContainer> provider = Mockito.mockStatic(AppInstallationContainer.class);
        provider.when(() -> AppInstallationContainer.create(installation))
                .thenReturn(installationContainer);

        Mockito.doReturn(id).when(installationContainer).getId();
        Mockito.doReturn(installation).when(installationContainer).unwrap();
        this.repository.put(installation);
        Mockito.verify(installationContainer, Mockito.times(1)).getId();

        assertSame(installation, this.repository.get(id));
        this.repository.remove(id);
        assertNull(this.repository.get(id));
    }

    @Test
    void getIds() {
        Set<Long> ids = Set.of(1L, 2L, 3L);

        for (long id : ids) {
            GHAppInstallation installation = Mockito.mock(GHAppInstallation.class);
            AppInstallationContainer installationContainer = Mockito.mock(AppInstallationContainer.class);

            MockedStatic<AppInstallationContainer> provider = Mockito.mockStatic(AppInstallationContainer.class);
            provider.when(() -> AppInstallationContainer.create(installation))
                    .thenReturn(installationContainer);

            Mockito.doReturn(id).when(installationContainer).getId();
            this.repository.put(installation);
            provider.close();
        }

        assertEquals(ids, this.repository.getIds());
    }
}