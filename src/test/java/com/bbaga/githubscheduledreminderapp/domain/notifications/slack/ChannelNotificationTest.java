package com.bbaga.githubscheduledreminderapp.domain.notifications.slack;

import com.bbaga.githubscheduledreminderapp.domain.configuration.Notification;
import com.bbaga.githubscheduledreminderapp.domain.configuration.SlackNotificationConfiguration;
import com.bbaga.githubscheduledreminderapp.domain.statistics.UrlBuilderInterface;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubIssue;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubPullRequest;
import com.bbaga.githubscheduledreminderapp.infrastructure.github.GitHubUser;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.model.block.DividerBlock;
import com.slack.api.model.block.HeaderBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import org.springframework.context.ApplicationEventPublisher;

import static com.slack.api.model.block.Blocks.section;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChannelNotificationTest {

    @Test
    void sendCalledWithEmptySets() throws SlackApiException, IOException {
        MethodsClient client = Mockito.mock(MethodsClient.class);
        ChannelMessageBuilderInterface mockMessageBuilder = Mockito.mock(ChannelMessageBuilderInterface.class);
        Mockito.when(mockMessageBuilder.createNoResultsMessage(Mockito.anyString())).thenReturn(section(s -> s.text(plainText("text"))));

        ApplicationEventPublisher mockEventPublisher = Mockito.mock(ApplicationEventPublisher.class);

        ChatPostMessageResponse result = Mockito.mock(ChatPostMessageResponse.class);

        ChannelNotification service = new ChannelNotification(client, mockMessageBuilder, mockEventPublisher);
        SlackNotificationConfiguration config = new SlackNotificationConfiguration();
        config.setChannel("test");
        Notification notification = new Notification();
        notification.setConfig(config);
        ArrayList<GitHubIssue> issues = new ArrayList<>();

        Mockito.when(client.chatPostMessage(Mockito.any(ChatPostMessageRequest.class))).thenReturn(result);
        service.send(new ChannelNotificationDataProvider.Data(notification, issues));
        Mockito.verify(client, Mockito.times(1)).chatPostMessage(Mockito.any(ChatPostMessageRequest.class));
    }

    @Test
    void sendCalled() throws IOException, SlackApiException {
        ApplicationEventPublisher mockEventPublisher = Mockito.mock(ApplicationEventPublisher.class);

        MethodsClient client = Mockito.mock(MethodsClient.class);
        UrlBuilderInterface urlBuilder = Mockito.mock(UrlBuilderInterface.class);
        ChannelMessageBuilderInterface messageBuilder = new ChannelMessageBuilder(urlBuilder);

        Mockito.when(urlBuilder.copy()).thenReturn(urlBuilder);

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

        ChatPostMessageResponse result = Mockito.mock(ChatPostMessageResponse.class);

        ChannelNotification service = new ChannelNotification(client, messageBuilder, mockEventPublisher);
        SlackNotificationConfiguration config = new SlackNotificationConfiguration();
        config.setChannel("test");
        Notification notification = new Notification();
        notification.setConfig(config);
        ArrayList<GitHubIssue> issues = new ArrayList<>();
        issues.add(issue);
        issues.add(pr);

        Mockito.when(client.chatPostMessage(Mockito.any(ChatPostMessageRequest.class))).thenReturn(result);
        service.send(new ChannelNotificationDataProvider.Data(notification, issues));

        ArgumentCaptor<ChatPostMessageRequest> postCaptor = ArgumentCaptor.forClass(ChatPostMessageRequest.class);

        // Verify message
        Mockito.verify(client, Mockito.times(1)).chatPostMessage(postCaptor.capture());

        ChatPostMessageRequest postMessageParams = postCaptor.getValue();

        List<LayoutBlock> blocks = postMessageParams.getBlocks();

        assertEquals(8, blocks.size());
        assertTrue(blocks.get(0) instanceof HeaderBlock);
        assertTrue(blocks.get(1) instanceof SectionBlock);
        assertTrue(blocks.get(2) instanceof DividerBlock);
        assertTrue(blocks.get(3) instanceof SectionBlock);
        assertTrue(blocks.get(4) instanceof SectionBlock);
        assertTrue(blocks.get(5) instanceof DividerBlock);
        assertTrue(blocks.get(6) instanceof SectionBlock);
        assertTrue(blocks.get(7) instanceof SectionBlock);

        // Verify PR methods
        Mockito.verify(pr, Mockito.times(1)).getAdditions();
        Mockito.verify(pr, Mockito.times(1)).getDeletions();
        Mockito.verify(pr, Mockito.times(1)).getTitle();
        Mockito.verify(pr, Mockito.times(1)).getUser();
        Mockito.verify(pr, Mockito.times(1)).getRepository();
        Mockito.verify(pr, Mockito.times(1)).getCreatedAt();

        // Verify Issue methods
        Mockito.verify(issue, Mockito.times(1)).getTitle();
        Mockito.verify(issue, Mockito.times(1)).getUser();
        Mockito.verify(issue, Mockito.times(1)).getRepository();
        Mockito.verify(issue, Mockito.times(1)).getCreatedAt();
    }
}
