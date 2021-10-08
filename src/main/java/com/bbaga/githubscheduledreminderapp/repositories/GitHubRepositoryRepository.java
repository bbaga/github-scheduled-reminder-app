package com.bbaga.githubscheduledreminderapp.repositories;

import java.util.HashMap;
import java.util.Map;

public class GitHubRepositoryRepository {
    private Map<String, Long> repositories = new HashMap<>();

    public void retainAll(HashMap<String, Long> set) {
        repositories.keySet().retainAll(set.keySet());
    }
}
