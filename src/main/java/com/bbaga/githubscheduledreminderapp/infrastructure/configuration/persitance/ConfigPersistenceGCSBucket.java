package com.bbaga.githubscheduledreminderapp.infrastructure.configuration.persitance;

import com.bbaga.githubscheduledreminderapp.domain.configuration.ConfigGraphNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigPersistenceGCSBucket implements ConfigPersistenceInterface {

    private final String filePath;
    private final String bucketName;
    private final Storage storage;

    public ConfigPersistenceGCSBucket(ConfigPersistenceGCSBucketSettings settings) {
        this.filePath = settings.getFilePath();
        this.bucketName = settings.getBucketName();
        Credentials credentials = null;

        try {
            if (!settings.getCredentialsJsonPath().isBlank()) {
                credentials = GoogleCredentials.fromStream(new FileInputStream(settings.getCredentialsJsonPath()));
            } else if (settings.getCredentialsJson().isBlank()) {
                credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(settings.getCredentialsJson().getBytes(StandardCharsets.UTF_8)));
            } else {
                throw new IllegalStateException("Could not configure the storage object with credentials.");
            }

            this.storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    @Override
    public ConcurrentHashMap<String, ConfigGraphNode> load() {
        ConcurrentHashMap<String, ConfigGraphNode> graph = new ConcurrentHashMap<String, ConfigGraphNode>();
        Blob blob = storage.get(BlobId.of(bucketName, filePath));

        if (blob != null && blob.exists()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            blob.downloadTo(outputStream);
            String stateJson = outputStream.toString();

            ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

            try {
                return objectMapper.readValue(stateJson, new TypeReference<ConcurrentHashMap<String, ConfigGraphNode>>() { });
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return graph;
    }

    @Override
    public void dump(ConcurrentHashMap<String, ConfigGraphNode> config) {
        ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();
        String json = "";
        try {
            json = objectMapper.writeValueAsString(config);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

        BlobId blobId = BlobId.of(bucketName, filePath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, json.getBytes(StandardCharsets.UTF_8));
    }
}
