package org.kohsuke.github;

import org.kohsuke.github.internal.Previews;

public class GitHubClientUtil {
    public static PagedSearchIterable<GHRepository> listRepositories(GitHub gitHub) {
        GitHubRequest request = ((Requester)((Requester)gitHub.createRequest().withPreview(Previews.MACHINE_MAN)).withUrlPath("/installation/repositories", new String[0])).build();
        return new PagedSearchIterable(gitHub, request, GHAppInstallationRepositoryResult.class);
    }

    private static class GHAppInstallationRepositoryResult extends SearchResult<GHRepository> {
        private GHRepository[] repositories;

        private GHAppInstallationRepositoryResult() {
        }

        GHRepository[] getItems(GitHub root) {
            return this.repositories;
        }
    }
}