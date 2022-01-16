package com.bbaga.githubscheduledreminderapp.domain.sources.github.webhooks;

import com.bbaga.githubscheduledreminderapp.domain.configuration.RepositoryInstallationEventListener;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.webhook.events.RepositoryInstallationEvent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class PrReviewRequestedListener implements ApplicationListener<RepositoryInstallationEvent> {
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(RepositoryInstallationEventListener.class);

    public PrReviewRequestedListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onApplicationEvent(RepositoryInstallationEvent event) {
        System.out.println("Handling event - " + event.getClass().getName());
        Payload payload = null;

        try {
            payload = objectMapper.readValue(event.getBody(), Payload.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (payload == null) {
            return;
        }

        return;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Payload {
        private String action;

        @JsonProperty("pull_request")
        private PullRequest pullRequest;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class PullRequest {
        private Author user;
        private String title;
        private Boolean draft;

        @JsonProperty("html_url")
        private String htmlUrl;

        public Author getUser() {
            return user;
        }

        public void setUser(Author user) {
            this.user = user;
        }

        public String getHtmlUrl() {
            return htmlUrl;
        }

        public void setHtmlUrl(String htmlUrl) {
            this.htmlUrl = htmlUrl;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Author {
        private String login;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }
    }
}
