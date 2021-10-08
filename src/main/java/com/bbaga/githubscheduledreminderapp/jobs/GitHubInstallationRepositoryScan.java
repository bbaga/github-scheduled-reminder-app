package com.bbaga.githubscheduledreminderapp.jobs;

import com.bbaga.githubscheduledreminderapp.InRepoConfig;
import com.bbaga.githubscheduledreminderapp.repositories.GitHubInstallationRepository;
import com.bbaga.githubscheduledreminderapp.repositories.GitHubRepositoryRepository;
import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.methods.params.chat.ChatPostMessageParams;
import com.hubspot.slack.client.models.Attachment;
import com.hubspot.slack.client.models.actions.Action;
import com.hubspot.slack.client.models.blocks.Block;
import com.hubspot.slack.client.models.blocks.Context;
import com.hubspot.slack.client.models.blocks.Divider;
import com.hubspot.slack.client.models.blocks.Section;
import com.hubspot.slack.client.models.blocks.elements.Button;
import com.hubspot.slack.client.models.blocks.objects.Text;
import com.hubspot.slack.client.models.blocks.objects.TextType;
import org.kohsuke.github.*;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class GitHubInstallationRepositoryScan implements Job {

    @Autowired
    private GitHub gitHub;

    @Autowired
    private GitHubInstallationRepository installationRepository;

    @Autowired
    private GitHubRepositoryRepository repositoryRepository;

    @Autowired
    private SlackClient slackClient;

    private Logger logger = LoggerFactory.getLogger(GitHubInstallationRepositoryScan.class);

    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("Starting Repository scanning for Installations");
        HashMap<String, Long> repositories = new HashMap<>();
        for (GHAppInstallation installation : installationRepository.getValues()) {
            try {
                GHAppInstallationToken token = installation.createToken().create();
                GitHub githubAuthAsInst = new GitHubBuilder()
                        .withAppInstallationToken(token.getToken())
                        .build();

                GitHub tempGH = GitHubClientUtil.getRoot(installation);
                GitHubClientUtil.setRoot(installation, githubAuthAsInst);

                for(GHRepository repo : installation.listRepositories()) {
                    repositories.put(repo.getFullName(), installation.getId());
                }

                GitHubClientUtil.setRoot(installation, tempGH);

                Set<String> repos = new HashSet<>(repositories.keySet());
                for (String repository : repos) {
                    GHRepository repo = githubAuthAsInst.getRepository(repository);

                    try {
                        GHContent content = repo.getFileContent(".demo-bot.yaml");
                        InRepoConfig inRepoConfig = new Yaml().loadAs(new String(content.read().readAllBytes(), StandardCharsets.UTF_8), InRepoConfig.class);
                        if (inRepoConfig.getEnabled()) {
                            logger.info("Repository found: {}", repo.getFullName());
                        }
                    } catch (GHFileNotFoundException e) {
                        repositories.remove(repository);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        repositoryRepository.retainAll(repositories);

        logger.info("Found {} installations and {} repositories", installationRepository.count(), repositories.size());

        List<Block> sections = new ArrayList<>();
        sections.add(Section.of(Text.of(TextType.MARKDOWN, "*Title*")));
        sections.add(Divider.builder().build());

        for (String repo : repositories.keySet()) {
            Section section = Section.of(Text.of(TextType.MARKDOWN, String.format("*%s*", repo)))
                    .withAccessory(Button.of(Text.of(TextType.PLAIN_TEXT, "Open"), repo).withUrl(String.format("https://github.com/%s", repo)));
            sections.add(section);
        }

        slackClient.postMessage(
                ChatPostMessageParams.builder()
                        .setBlocks(sections)
                        .setChannelId("bottest")
                        .build()
        ).join();
    }
}
