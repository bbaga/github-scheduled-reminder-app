package com.bbaga.githubscheduledreminderapp.application.config;

import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubBuilderFactory;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.extras.authorization.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GitHubConfig {

  @Value("${application.github.endpoint}")
  private String gitHubEndpoint;

  @Value("${application.github.app.cert}")
  private String gitHubAppCert;

  @Value("${application.github.app.certFile}")
  private String gitHubAppCertFile = "";

  @Value("${application.github.app.id}")
  private String githubApplicationId;

  @Bean
  @Qualifier("application.github.endpoint")
  public String getGitHubEndpoint() {
    return this.gitHubEndpoint;
  }

  @Bean
  public GitHubBuilderFactory getGitHubBuilderFactory(@Qualifier("application.github.endpoint") String gitHubEndpoint) {
    return new GitHubBuilderFactory(gitHubEndpoint);
  }

  @Bean
  public GitHub getGitHubClient(GitHubBuilderFactory gitHubBuilderFactory) throws Exception {
    if (!gitHubAppCertFile.isEmpty() && gitHubAppCert.isEmpty()) {
      try(FileInputStream inputStream = new FileInputStream(gitHubAppCertFile)) {
        gitHubAppCert = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
      }
    }

    if (gitHubAppCert.isEmpty()) {
      throw new IllegalStateException("GitHub certificate must be configured, see the GITHUB_APP_CERT and GITHUB_APP_CERT_FILE environment variables.");
    }

    String privateKeyPEM = gitHubAppCert
        .replace("-----BEGIN PRIVATE KEY-----", "")
        .replaceAll(System.lineSeparator(), "")
        .replace("-----END PRIVATE KEY-----", "")
        .replaceAll(" ", "");

    return gitHubBuilderFactory.create().withAuthorizationProvider(new JWTTokenProvider(githubApplicationId, privateKeyPEM)).build();
  }
}
