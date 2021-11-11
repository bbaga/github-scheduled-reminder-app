package com.bbaga.githubscheduledreminderapp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GitHubEventHandler {

    GitHubEventHandler() {

    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }
}
