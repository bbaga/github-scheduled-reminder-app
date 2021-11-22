package com.bbaga.githubscheduledreminderapp.controllers;

import com.bbaga.githubscheduledreminderapp.infrastructure.github.webhook.EventPublisher;
import org.kohsuke.github.GitHub;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GitHubEventHandler {

    private final EventPublisher eventPublisher;

    GitHubEventHandler(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @PostMapping("/github/webhook")
    public String webhook(@RequestHeader("X-GitHub-Event") String event, @RequestBody String body) {
        eventPublisher.publishEvent(this, event, body);

        return "ACK";
    }
}
