package com.bbaga.githubscheduledreminderapp.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class InRepoConfigParser {
    private final ObjectMapper mapper;
    private final String configFilePath;

    public InRepoConfigParser(ObjectMapper mapper, String configFilePath) {
        this.mapper = mapper;
        this.configFilePath = configFilePath;
    }

    public InRepoConfig getFrom(GHRepository repository) throws IOException {
        GHContent content = repository.getFileContent(configFilePath);
        return mapper.readValue(new String(content.read().readAllBytes(), StandardCharsets.UTF_8), InRepoConfig.class);
    }
}
