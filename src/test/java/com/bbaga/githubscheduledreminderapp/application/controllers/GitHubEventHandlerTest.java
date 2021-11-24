package com.bbaga.githubscheduledreminderapp.application.controllers;

import com.bbaga.githubscheduledreminderapp.infrastructure.github.webhook.EventPublisher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GitHub;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(GitHubEventHandler.class)
class GitHubEventHandlerTest {

    @TestConfiguration
    static class GitHubEventHandlerTestConfiguration {
        @Bean
        public GitHub getGitHubClient() {
            return Mockito.mock(GitHub.class);
        }

        @Bean
        public EventPublisher getGitHubEventPublisher() {
            return Mockito.mock(EventPublisher.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventPublisher mockEventPublisher;

    @Test
    public void testHello() throws Exception {
        String endpoint = "/github/webhook";
        this.mockMvc.perform(
                post(endpoint).content(
"""
{
  "action": "added"
}
"""
                ).header("X-GitHub-Event", "installation_repositories")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("ACK")));

        ArgumentCaptor<Object> sourceCaptor = ArgumentCaptor.forClass(Object.class);
        ArgumentCaptor<String> typeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(mockEventPublisher, Mockito.times(1))
            .publishEvent(sourceCaptor.capture(), typeCaptor.capture(), bodyCaptor.capture());

        Object capturedSource = sourceCaptor.getValue();
        String capturedType = typeCaptor.getValue();
        String capturedBody = bodyCaptor.getValue();

        Assertions.assertTrue(capturedSource instanceof GitHubEventHandler);
        assertEquals("installation_repositories", capturedType);
        Assertions.assertTrue(capturedBody.contains("\"action\": \"added\""));
    }
}
