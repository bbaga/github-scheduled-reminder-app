package com.bbaga.githubscheduledreminderapp.application.controllers;

import com.bbaga.githubscheduledreminderapp.domain.statistics.AggregatedStatisticsStorage;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubAppInstallation;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubBuilderFactory;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.repositories.GitHubInstallationRepository;
import org.kohsuke.github.GHAppInstallationToken;
import org.kohsuke.github.GHRateLimit;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubClientUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class Statistics {

    private final AggregatedStatisticsStorage statisticsStorage;
    private final GitHubBuilderFactory github;
    private final GitHubInstallationRepository installations;

    Statistics(AggregatedStatisticsStorage statisticsStorage, GitHubBuilderFactory github, GitHubInstallationRepository installations) {
        this.statisticsStorage = statisticsStorage;
        this.github = github;
        this.installations = installations;
    }

    @GetMapping("/statistics/fetch")
    public Map<String, AtomicLong> fetch() {
        HashMap<String, AtomicLong> aggregates = new HashMap<>(statisticsStorage.getAggregates());
        statisticsStorage.reset();
        return enrichWithRateLimitInfo(aggregates);
    }

    @GetMapping("/statistics/show")
    public Map<String, AtomicLong> show() {
        return enrichWithRateLimitInfo(statisticsStorage.getAggregates());
    }

    private Map<String, AtomicLong> enrichWithRateLimitInfo(Map<String, AtomicLong> aggregates) {
        installations.getOrgs().forEach(org -> {
            GitHubAppInstallation installation = installations.get(installations.getIdByOrg(org));

            long coreRemaining = 0;
            long searchRemaining = 0;
            long graphqlRemaining = 0;
            long manifestRemaining = 0;
            long coreLimit = 0;
            long searchLimit = 0;
            long graphqlLimit = 0;
            long manifestLimit = 0;

            try {
                GHAppInstallationToken token = installation.unwrap().createToken().create();
                GitHub githubAuthAsInst = github.create()
                        .withAppInstallationToken(token.getToken())
                        .build();
                GHRateLimit rateLimit = GitHubClientUtil.getRateLimit(githubAuthAsInst);

                coreRemaining = rateLimit.getCore().getRemaining();
                searchRemaining = rateLimit.getSearch().getRemaining();
                graphqlRemaining = rateLimit.getGraphQL().getRemaining();
                manifestRemaining = rateLimit.getIntegrationManifest().getRemaining();

                coreLimit = rateLimit.getCore().getLimit();
                searchLimit = rateLimit.getSearch().getLimit();
                graphqlLimit = rateLimit.getGraphQL().getLimit();
                manifestLimit = rateLimit.getIntegrationManifest().getLimit();
            } catch (IOException e) {
                e.printStackTrace();
            }

            aggregates.put("github.app.installation." + org + ".rate-limit.core.remaining", new AtomicLong(coreRemaining));
            aggregates.put("github.app.installation." + org + ".rate-limit.search.remaining", new AtomicLong(searchRemaining));
            aggregates.put("github.app.installation." + org + ".rate-limit.graphql.remaining", new AtomicLong(graphqlRemaining));
            aggregates.put("github.app.installation." + org + ".rate-limit.integration-manifest.remaining", new AtomicLong(manifestRemaining));

            aggregates.put("github.app.installation." + org + ".rate-limit.core.limit", new AtomicLong(coreLimit));
            aggregates.put("github.app.installation." + org + ".rate-limit.search.limit", new AtomicLong(searchLimit));
            aggregates.put("github.app.installation." + org + ".rate-limit.graphql.limit", new AtomicLong(graphqlLimit));
            aggregates.put("github.app.installation." + org + ".rate-limit.integration-manifest.limit", new AtomicLong(manifestLimit));
        });

        return aggregates;
    }
}
