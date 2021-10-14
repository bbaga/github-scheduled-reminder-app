package com.bbaga.githubscheduledreminderapp;

import com.bbaga.githubscheduledreminderapp.repositories.GitHubInstallationRepository;
import org.kohsuke.github.GHAppInstallationToken;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

@Service
public class GitHubAppInstallationService {
    private final GitHubInstallationRepository installationRepository;
    private final HashMap<Long, GitHub> installationClientCache = new HashMap<>();

    public GitHubAppInstallationService(GitHubInstallationRepository installationRepository) {
        this.installationRepository = installationRepository;
    }

    public GitHub getClientByInstallationId(Long installationId) {
        if (!installationClientCache.containsKey(installationId)) {
            GHAppInstallationToken token;
            GitHub installationClient;
            try {
                token = installationRepository.get(installationId).createToken().create();
                installationClient = new GitHubBuilder()
                        .withAppInstallationToken(token.getToken())
                        .build();
            } catch (IOException e) {
                throw new RuntimeException(String.format("Could not crate client for installation %d", installationId));
            }

            installationClientCache.put(installationId, installationClient);

            return installationClient;
        }

        return installationClientCache.get(installationId);
    }
}
