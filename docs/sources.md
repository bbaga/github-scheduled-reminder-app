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
    type: slack/scheduled/channel
    config:
      schedule: "0 0 12 * * ?"
      timezone: "My/TimeZone"
      channel: "some-slack-channel"
      sources:
        - type: repository-issues
          filters:
            - type: label-filter
              exclude-labels:
                - acknowledged
                - pinned
                - wontfix
              expiry-days: 90
        - type: repository-prs
          filters:
            - type: draft-filter
              include-drafts: false
              expiry-days: 90
        - type: search-prs-by-reviewers
          users:
            - foo
            - bar
          teams:
            - my-org/my-team
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
          teams:
            - my-org/my-team
        - type: search-issues
          query: "label:\"help wanted\""
```

## Repository Issues
Pulls open issues from the repositories. This source is added as a default, it is configured with `label-filter` to ignore.

```yaml
config:
  sources:
    - type: repository-issues
      filters:
        - type: label-filter
          exclude-labels:
            - acknowledged
          expiry-days: 90
```

## Repository PRs
Pulls open pull-requests from the repositories. This source is added as a default, it is configured with `draft-filter` and draft PRs will be filtered out.

```yaml
config:
  sources:
    - type: repository-prs
      filters:
        - type: draft-filter
          include-drafts: false
          expiry-days: 90
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
      teams:
        - my-org/my-team
```

## Search issues with a custom query
Finds PRs (and Issues) based on the custom query.

```yaml
config:
  sources:
    - type: search-issues
      query: "label:\"help wanted\""
```

## Filters

### Draft-filter
This filter will remove the draft PRs from the report.
By default this filter will expire never expire.

```yaml
filters:
  - type: draft-filter
    include-drafts: false
    expiry-days: 90
```

### Label-filter
This filter will remove the PRs and Issues that are not labeled by one of the included labels
or exclude PRs and Issues that are labeled by one of the exclude labels.
Do not use both the include and exclude.
If a label exist in both lists the include will check first and include the label.
By default this filter will expire never expire.

```yaml
filters:
  - type: label-filter
    include:
      - include-label
    exclude:  
      - exclude-label
    exclude-labels: (Deprecated to match other labels)
      - exclude-label
    expiry-days: 90
```

### Author-filter
This filter will remove the PRs and Issues that are not authored by one of the included authors 
or exclude PRs and Issues that are authored by one of the exclude authors.
Do not use both the include and exclude.
If and author exist in both lists the include will check first and include the author.
By default this filter will expire never expire. 

```yaml
filters:
  - type: author-filter
    include:
      - usernameA
      - usernameB
    exclude:
      - usernameA
      - usernameB
    expiry-days: 90
```

### Path-filter
This filter will remove the PRs and Issues that do not have a file in the include paths
or exclude PRs and Issues that do have a path in the exclude paths.
Do not use both the include and exclude.
If a path exist in both lists the include will check first and include the path.
Because this uses a starts with you should not include a directory and then exclude files within that sub-directoey.
Instead just exclude the files and the parent will be automatically included.
By default this filter will expire never expire.

```yaml
filters:
  - type: path-filter
    include:
      - src/foo
    exclude:
      - src/foo/bar
    expiry-days: 90
```

The example above would match the following paths:

 - `src/foo`
 - `src/foo.txt`
 - `src/foo/bar`
