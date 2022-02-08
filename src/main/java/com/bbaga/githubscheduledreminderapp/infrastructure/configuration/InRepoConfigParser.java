package com.bbaga.githubscheduledreminderapp.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.yaml.snakeyaml.Yaml;

public class InRepoConfigParser {
    private final ObjectMapper mapper;
    private final String configFilePath;

    public InRepoConfigParser(ObjectMapper mapper, String configFilePath) {
        this.mapper = mapper;
        this.configFilePath = configFilePath;
    }

    public InRepoConfig getFrom(GHRepository repository) throws IOException {
        GHContent content = repository.getFileContent(configFilePath);

        // Trickery because Jackson doesn't handle YAML anchors
        Object tmpYaml = new Yaml().loadAs(content.read(), Object.class);
        String prettyYaml = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tmpYaml);

        return mapper.readValue(prettyYaml, InRepoConfig.class);
    }
}
