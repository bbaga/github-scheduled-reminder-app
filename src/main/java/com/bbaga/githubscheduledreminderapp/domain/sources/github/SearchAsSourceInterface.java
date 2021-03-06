package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.SourceConfig;
import java.util.List;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import java.io.IOException;

public interface SearchAsSourceInterface<T> {
    void configure(SourceConfig sourceConfig);
    List<T> get(GHRepository repo, GitHub client) throws IOException;
}
