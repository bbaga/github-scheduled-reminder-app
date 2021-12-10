package com.bbaga.githubscheduledreminderapp.infrastructure.github.repositories;

import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubAppInstallation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.GHAppInstallation;
import org.kohsuke.github.GHUser;
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
        GHUser account = Mockito.mock(GHUser.class);
        GitHubAppInstallation installationContainer = Mockito.mock(GitHubAppInstallation.class);

        MockedStatic<GitHubAppInstallation> provider = Mockito.mockStatic(GitHubAppInstallation.class);
        provider.when(() -> GitHubAppInstallation.create(installation))
                .thenReturn(installationContainer);

        Mockito.doReturn(id).when(installationContainer).getId();
        Mockito.doReturn(account).when(installationContainer).getAccount();
        Mockito.doReturn("bbaga").when(account).getLogin();
        Mockito.doReturn(installation).when(installationContainer).unwrap();
        this.repository.put(installation);
        Mockito.verify(installationContainer, Mockito.times(2)).getId();

        assertSame(installation, this.repository.get(id).unwrap());
        this.repository.remove(id);
        assertNull(this.repository.get(id));
    }

    @Test
    void getIds() {
        Set<Long> ids = Set.of(1L, 2L, 3L);

        for (long id : ids) {
            GHAppInstallation installation = Mockito.mock(GHAppInstallation.class);
            GHUser account = Mockito.mock(GHUser.class);
            GitHubAppInstallation installationContainer = Mockito.mock(GitHubAppInstallation.class);

            MockedStatic<GitHubAppInstallation> provider = Mockito.mockStatic(GitHubAppInstallation.class);
            provider.when(() -> GitHubAppInstallation.create(installation))
                    .thenReturn(installationContainer);

            Mockito.doReturn(id).when(installationContainer).getId();
            Mockito.doReturn(account).when(installationContainer).getAccount();
            Mockito.doReturn("bbaga").when(account).getLogin();
            this.repository.put(installation);
            provider.close();
        }

        assertEquals(ids, this.repository.getIds());
    }
}
