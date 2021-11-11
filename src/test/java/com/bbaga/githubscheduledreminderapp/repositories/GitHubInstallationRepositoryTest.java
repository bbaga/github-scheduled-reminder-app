package com.bbaga.githubscheduledreminderapp.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.GHAppInstallation;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
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
        Mockito.doReturn(id).when(installation).getId();
        this.repository.put(installation);
        Mockito.verify(installation, Mockito.times(1)).getId();

        assertSame(installation, this.repository.get(id));
        this.repository.remove(id);
        assertNull(this.repository.get(id));
    }

    @Test
    void getIds() {
        Set<Long> ids = Set.of(1L, 2L, 3L);

        GHAppInstallation installation;
        for (long id : ids) {
            installation = Mockito.mock(GHAppInstallation.class);
            Mockito.doReturn(id).when(installation).getId();
            this.repository.put(installation);
        }

        assertEquals(ids, this.repository.getIds());
    }
}