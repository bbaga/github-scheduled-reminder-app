package com.bbaga.githubscheduledreminderapp.configuration;

import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class InRepoConfigParser {
    private final Yaml yaml;
    private final String configFilePath;

    public InRepoConfigParser(Yaml yaml, String configFilePath) {
        this.yaml = yaml;
        this.configFilePath = configFilePath;
    }

    public InRepoConfig getFrom(GHRepository repository) throws IOException {
        GHContent content = repository.getFileContent(configFilePath);
        return yaml.loadAs(new String(content.read().readAllBytes(), StandardCharsets.UTF_8), InRepoConfig.class);
    }
}
