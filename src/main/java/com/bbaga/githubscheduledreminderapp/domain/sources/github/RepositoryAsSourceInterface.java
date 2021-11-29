package com.bbaga.githubscheduledreminderapp.domain.sources.github;

import com.bbaga.githubscheduledreminderapp.domain.configuration.sources.Source;
import org.kohsuke.github.GHRepository;

import java.io.IOException;
import java.util.ArrayList;

public interface RepositoryAsSourceInterface <T> {
    void configure(Source source);
    ArrayList<T> get(GHRepository repository) throws IOException;
}
