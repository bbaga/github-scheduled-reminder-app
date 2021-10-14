package com.bbaga.githubscheduledreminderapp.notifications.slack;

import com.bbaga.githubscheduledreminderapp.configuration.Notification;
import com.hubspot.algebra.Result;
import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.models.response.SlackError;
import com.hubspot.slack.client.models.response.chat.ChatPostMessageResponse;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class ChannelNotificationTest {

    @Test
    void sendCalledWithEmptySets() {
        SlackClient client = Mockito.mock(SlackClient.class);
        //noinspection unchecked
        Result<ChatPostMessageResponse, SlackError> result = (Result<ChatPostMessageResponse, SlackError>) Mockito.mock(Result.class);
        CompletableFuture<Result<ChatPostMessageResponse, SlackError>> future = new CompletableFuture<>();
        future.complete(result);

        ChannelNotification service = new ChannelNotification(client);
        HashMap<String, String> config = new HashMap<>();
        config.put("channel", "test");
        Notification notification = new Notification();
        notification.setConfig(config);
        Set<GHIssue> issues = new HashSet<>();
        Set<GHIssue> pullRequests = new HashSet<>();

        Mockito.when(client.postMessage(Mockito.any())).thenReturn(future);
        service.send(new ChannelNotificationDataProvider.Data(notification, issues, pullRequests));
        Mockito.verify(client, Mockito.times(1)).postMessage(Mockito.any());

    }

    @Test
    void sendCalled() {
        SlackClient client = Mockito.mock(SlackClient.class);
        GHIssue issue = Mockito.mock(GHIssue.class);
        GHUser user = Mockito.mock(GHUser.class);
        GHRepository repository = Mockito.mock(GHRepository.class);
        GHPullRequest pullRequest = Mockito.mock(GHPullRequest.class);
        GHIssue pr = Mockito.mock(GHIssue.class);
        Mockito.when(user.getLogin()).thenReturn("userLogin");
        Mockito.when(repository.getFullName()).thenReturn("org/repo");


        try {
            Mockito.when(issue.getTitle()).thenReturn("Issue title");
            Mockito.when(issue.getUser()).thenReturn(user);
            Mockito.when(issue.getHtmlUrl()).thenReturn(new URL("https://google.com"));
            Mockito.when(issue.getNodeId()).thenReturn("1234567");
            Mockito.when(issue.getRepository()).thenReturn(repository);

            Mockito.when(pr.getTitle()).thenReturn("PR title");
            Mockito.when(pr.getNumber()).thenReturn(1);
            Mockito.when(pr.getUser()).thenReturn(user);
            Mockito.when(pr.getHtmlUrl()).thenReturn(new URL("https://google.com"));
            Mockito.when(pr.getNodeId()).thenReturn("1234567");
            Mockito.when(pr.getRepository()).thenReturn(repository);
            Mockito.when(repository.getPullRequest(1)).thenReturn(pullRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //noinspection unchecked
        Result<ChatPostMessageResponse, SlackError> result = (Result<ChatPostMessageResponse, SlackError>) Mockito.mock(Result.class);
        CompletableFuture<Result<ChatPostMessageResponse, SlackError>> future = new CompletableFuture<>();
        future.complete(result);

        ChannelNotification service = new ChannelNotification(client);
        HashMap<String, String> config = new HashMap<>();
        config.put("channel", "test");
        Notification notification = new Notification();
        notification.setConfig(config);
        Set<GHIssue> issues = new HashSet<>();
        issues.add(issue);
        Set<GHIssue> pullRequests = new HashSet<>();
        pullRequests.add(pr);

        Mockito.when(client.postMessage(Mockito.any())).thenReturn(future);
        service.send(new ChannelNotificationDataProvider.Data(notification, issues, pullRequests));
        Mockito.verify(client, Mockito.times(1)).postMessage(Mockito.any());

    }
}