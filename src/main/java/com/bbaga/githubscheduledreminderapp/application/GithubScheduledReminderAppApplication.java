package com.bbaga.githubscheduledreminderapp.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GithubScheduledReminderAppApplication {

	public static void main(String[] args) {
		System.setProperty("spring.devtools.restart.enabled", "false");
		SpringApplication.run(GithubScheduledReminderAppApplication.class, args);
	}

}
