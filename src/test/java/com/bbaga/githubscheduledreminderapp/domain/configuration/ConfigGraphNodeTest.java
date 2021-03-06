package com.bbaga.githubscheduledreminderapp.domain.configuration;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ConfigGraphNodeTest {

    private static Stream<Arguments> testSubjectProvider() {
        final long installationId = 1;
        final String notificationName = "notification-name";
        final Notification notification = new Notification(notificationName, "", new SlackNotificationConfiguration());
        final Instant instant = Instant.now();

        return Stream.of(
                Arguments.of(new ConfigGraphNode(installationId, notification, instant), installationId, notificationName, instant)
        );
    }

    @ParameterizedTest
    @MethodSource("testSubjectProvider")
    void simpleGetterTests(ConfigGraphNode node, long installationId, String notificationName, Instant instant) {
        assertEquals(installationId, node.getInstallationId());
        assertEquals(notificationName, node.getNotification().getName());
        assertEquals(instant, node.getLastSeenAt());
    }

    @ParameterizedTest
    @MethodSource("testSubjectProvider")
    void putRepository(ConfigGraphNode node) {
        long installationId = 12;
        String repoName = "repository-name";
        Instant lastSeen = Instant.now();
        RepositoryRecord repoRecord = new RepositoryRecord(repoName, installationId, lastSeen, null);

        assertEquals(0, node.getRepositories().size());
        node.putRepository(repoRecord);
        assertEquals(1, node.getRepositories().size());
        assertEquals(repoName, node.getRepositories().get(repoRecord.hashCode()).getRepository());
    }
}
