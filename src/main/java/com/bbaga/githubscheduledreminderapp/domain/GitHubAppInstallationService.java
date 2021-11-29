package com.bbaga.githubscheduledreminderapp.domain;

import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubBuilderFactory;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.repositories.GitHubInstallationRepository;
import org.kohsuke.github.GHAppInstallation;
import org.kohsuke.github.GHAppInstallationToken;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.authorization.AuthorizationProvider;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;

@Service
public class GitHubAppInstallationService {
    private final GitHub gitHub;
    private final GitHubBuilderFactory gitHubBuilderFactory;
    private final GitHubInstallationRepository installationRepository;
    private final HashMap<Long, GitHub> installationClientCache = new HashMap<>();

    public GitHubAppInstallationService(
        GitHub gitHub,
        GitHubBuilderFactory gitHubBuilderFactory,
        GitHubInstallationRepository installationRepository
    ) {
        this.gitHub = gitHub;
        this.gitHubBuilderFactory = gitHubBuilderFactory;
        this.installationRepository = installationRepository;
    }

    public GitHub getGitHub() {
        return gitHub;
    }

    public GitHub getClientByInstallationId(Long installationId) {
        if (!installationClientCache.containsKey(installationId)) {
            GitHub installationClient;
            try {
                class AuthProv implements AuthorizationProvider {
                    private GHAppInstallationToken token;
                    private final GHAppInstallation installation;

                    public AuthProv(GHAppInstallation installation) {
                        this.installation = installation;
                    }

                    @Override
                    public String getEncodedAuthorization() throws IOException {
                        if (token == null || Instant.now().plusSeconds(300).isAfter(this.token.getExpiresAt().toInstant())) {
                            token = installation.createToken().create();
                        }

                        return String.format("token %s", token.getToken());
                    }
                }

                installationClient = gitHubBuilderFactory.create()
                        .withAuthorizationProvider(new AuthProv(installationRepository.get(installationId)))
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
