package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.Source;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.ArrayList;

public interface SearchAsSourceInterface<T> {
    void configure(Source source);
    ArrayList<T> get(GHRepository repo, GitHub client) throws IOException;
}
