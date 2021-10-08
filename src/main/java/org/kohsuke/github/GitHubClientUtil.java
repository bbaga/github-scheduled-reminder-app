package org.kohsuke.github;

public class GitHubClientUtil {
    public static <T extends GitHubInteractiveObject> GitHub getRoot(T githubObj) {
        return githubObj.root;
    }

    public static <T extends GitHubInteractiveObject> void setRoot(T githubObj, GitHub root) {
        githubObj.root = root;
    }
}