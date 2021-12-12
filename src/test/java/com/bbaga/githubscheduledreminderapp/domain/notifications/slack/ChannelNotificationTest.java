package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.bbaga.githubscheduledreminderapp.domain.configuration.Notification;
import com.bbaga.githubscheduledreminderapp.domain.configuration.SlackNotificationConfiguration;
import com.bbaga.githubscheduledreminderapp.domain.statistics.UrlBuilderInterface;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubUser;
import com.hubspot.algebra.Result;
import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.methods.params.chat.ChatPostMessageParams;
import com.hubspot.slack.client.models.blocks.Block;
import com.hubspot.slack.client.models.blocks.Divider;
import com.hubspot.slack.client.models.blocks.Header;
import com.hubspot.slack.client.models.blocks.Section;
import com.hubspot.slack.client.models.response.SlackError;
import com.hubspot.slack.client.models.response.chat.ChatPostMessageResponse;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChannelNotificationTest {

    @Test
    void sendCalledWithEmptySets() {
        SlackClient client = Mockito.mock(SlackClient.class);
        UrlBuilderInterface urlBuilder = Mockito.mock(UrlBuilderInterface.class);
        //noinspection unchecked
        Result<ChatPostMessageResponse, SlackError> result = (Result<ChatPostMessageResponse, SlackError>) Mockito.mock(Result.class);
        CompletableFuture<Result<ChatPostMessageResponse, SlackError>> future = new CompletableFuture<>();
        future.complete(result);

        ChannelNotification service = new ChannelNotification(client, urlBuilder);
        SlackNotificationConfiguration config = new SlackNotificationConfiguration();
        config.setChannel("test");
        Notification notification = new Notification();
        notification.setConfig(config);
        ArrayList<GitHubIssue> issues = new ArrayList<>();

        Mockito.when(client.postMessage(Mockito.any())).thenReturn(future);
        service.send(new ChannelNotificationDataProvider.Data(notification, issues));
        Mockito.verify(client, Mockito.times(1)).postMessage(Mockito.any());

    }

    @Test
    void sendCalled() throws IOException {
        SlackClient client = Mockito.mock(SlackClient.class);
        UrlBuilderInterface urlBuilder = Mockito.mock(UrlBuilderInterface.class);
        GitHubIssue issue = Mockito.mock(GitHubIssue.class);
        GitHubUser user = Mockito.mock(GitHubUser.class);
        GHRepository repository = Mockito.mock(GHRepository.class);
        GitHubPullRequest pr = Mockito.mock(GitHubPullRequest.class);
        Mockito.when(user.getLogin()).thenReturn("userLogin");
        Mockito.when(repository.getFullName()).thenReturn("org/repo");

        Mockito.when(issue.getTitle()).thenReturn("Issue title");
        Mockito.when(issue.getUser()).thenReturn(user);
        Mockito.when(issue.getHtmlUrl()).thenReturn(new URL("https://google.com"));
        Mockito.when(issue.getNodeId()).thenReturn("1234567");
        Mockito.when(issue.getRepository()).thenReturn(repository);
        Mockito.when(issue.getCreatedAt()).thenReturn(Date.from(Instant.now()));

        Mockito.when(pr.getTitle()).thenReturn("PR title");
        Mockito.when(pr.getNumber()).thenReturn(1);
        Mockito.when(pr.getUser()).thenReturn(user);
        Mockito.when(pr.getHtmlUrl()).thenReturn(new URL("https://google.com"));
        Mockito.when(pr.getNodeId()).thenReturn("1234567");
        Mockito.when(pr.getRepository()).thenReturn(repository);
        Mockito.when(pr.getCreatedAt()).thenReturn(Date.from(Instant.now()));
        Mockito.when(pr.getMergeableState()).thenReturn("clean");

        //noinspection unchecked
        Result<ChatPostMessageResponse, SlackError> result = (Result<ChatPostMessageResponse, SlackError>) Mockito.mock(Result.class);
        CompletableFuture<Result<ChatPostMessageResponse, SlackError>> future = new CompletableFuture<>();
        future.complete(result);

        ChannelNotification service = new ChannelNotification(client, urlBuilder);
        SlackNotificationConfiguration config = new SlackNotificationConfiguration();
        config.setChannel("test");
        Notification notification = new Notification();
        notification.setConfig(config);
        ArrayList<GitHubIssue> issues = new ArrayList<>();
        issues.add(issue);
        issues.add(pr);

        Mockito.when(client.postMessage(Mockito.any())).thenReturn(future);
        service.send(new ChannelNotificationDataProvider.Data(notification, issues));

        ArgumentCaptor<ChatPostMessageParams> postCaptor = ArgumentCaptor.forClass(ChatPostMessageParams.class);

        // Verify message
        Mockito.verify(client, Mockito.times(1)).postMessage(postCaptor.capture());

        ChatPostMessageParams postMessageParams = postCaptor.getValue();

        List<Block> blocks = postMessageParams.getBlocks();

        assertEquals(8, blocks.size());
        assertTrue(blocks.get(0) instanceof Header);
        assertTrue(blocks.get(1) instanceof Section);
        assertTrue(blocks.get(2) instanceof Divider);
        assertTrue(blocks.get(3) instanceof Section);
        assertTrue(blocks.get(4) instanceof Section);
        assertTrue(blocks.get(5) instanceof Divider);
        assertTrue(blocks.get(6) instanceof Section);
        assertTrue(blocks.get(7) instanceof Section);

        // Verify PR methods
        Mockito.verify(pr, Mockito.times(1)).getAdditions();
        Mockito.verify(pr, Mockito.times(1)).getDeletions();
        Mockito.verify(pr, Mockito.times(1)).getTitle();
        Mockito.verify(pr, Mockito.times(1)).getUser();
        Mockito.verify(pr, Mockito.times(1)).getRepository();
        Mockito.verify(pr, Mockito.times(2)).getCreatedAt();

        // Verify Issue methods
        Mockito.verify(issue, Mockito.times(1)).getTitle();
        Mockito.verify(issue, Mockito.times(1)).getUser();
        Mockito.verify(issue, Mockito.times(1)).getRepository();
        Mockito.verify(issue, Mockito.times(2)).getCreatedAt();

    }
}
