# Sources

## What are sources?
With the source configurations we can tell the application what we would like to see in the reports, how it should pull the information and how to filter the results.

For example, by default, draft PRs will be filtered out as work in progress is rarely interesting, or in shared repositories, we might be interested in PRs where a team member is asked to do the review.

> ⚠ Once a source is configured, all other default sources will be disabled!

## Examples

```yaml
enabled: true
notifications:
  - name: something
    schedule: "0 0 12 * * ?"
    type: slack/channel
    timezone: "My/TimeZone"
    config:
      channel: "some-slack-channel"
      sources:
        - type: repository-issues
        - type: repository-prs
          filters:
            - type: draft-filter
              include-drafts: true
        - type: search-prs-by-reviewers
          users:
            - foo
            - bar
        - type: search-issues
          query: "label:\"help wanted\""
```

```yaml
enabled: true
notifications:
  - extending:
      repository: some/repository
      name: something
    config:
      sources:
        - type: search-prs-by-reviewers
          users:
            - foo
            - bar
        - type: search-issues
          query: "label:\"help wanted\""
```

## Repository Issues
Pulls open issues from the repositories. This source is added as a default.

```yaml
config:
  sources:
    - type: repository-issues
```

## Repository PRs
Pulls open pull-requests from the repositories. This source is added as a default, it is configured with `draft-filter` and draft PRs will be filtered out.

```yaml
config:
  sources:
    - type: repository-prs
      filters:
        - type: draft-filter
          include-drafts: true #default
```

## Search PRs by reviewers
Finds PRs where at least one of the specified users are in the list of reviewers.

```yaml
config:
  sources:
    - type: search-prs-by-reviewers
      users:
        - user-a
        - user-b
```

## Search issues with a custom query
Finds PRs (and Issues) based on the custom query.

```yaml
config:
  sources:
    - type: search-issues
       query: "label:\"help wanted\""
```
