# Template configuration options

## What is the template configuration?
The application comes with a default template for each notification type, but in case you don't like it, you can override it with your own settings.

## Slack notification template
### Example
#### Change texts
```yaml
enabled: true
notifications:
  - name: custom-texts
    type: slack/scheduled/channel
    config:
      schedule: "0 0 12 * * ?"
      timezone: "My/TimeZone"
      channel: "some-slack-channel"
      template-config:
        header-main: "Open Issues and Pull Requests"
        header-prs: "Pull Requests ($counter)"
        header-issues: "Issues ($counter)"
        overflow-format: "$showing last $showing from $from"
        line-prs: "*$title* in *$repository*, age: $age, :heavy_minus_sign: $deletions :heavy_plus_sign: $additions $button"
        line-issues: "*$title* in *$repository*, age: $age$button"
        no-results: "There are no open issues or PRs"
      
```

#### Free text mode
```yaml
enabled: true
notifications:
  - name: free-text
    type: slack/scheduled/channel
    config:
      schedule: "0 0 12 * * ?"
      timezone: "My/TimeZone"
      channel: "some-slack-channel"
      template-config:
        mode: free-text
        header-main: ":mega: *Open Issues and Pull Requests*"
        header-prs: "\n*Pull Requests ($counter)*"
        header-issues: "\n*Issues ($counter)*"
        overflow-format: "$showing last $showing from $from"
        line-prs: " • <$link|$title> in *$repository*, age: $age, :heavy_minus_sign: $deletions :heavy_plus_sign: $additions"
        line-issues: " • <$link|$title> in *$repository*, age: $age"
        no-results: "There are no open issues or PRs"
      
```

### Available options

#### `mode`
Controls the overall formatting approach. The default is using Slack's block components. 

Possible values: `free-text`, `block`

Default: `block`

#### `header-main`
The first line of the notification, serves as a title.

Possible values: Any `string`

#### `header-issues`
Shown when there are issues to show, acts as section title for the issues.

Possible values: Any `string`

##### Placeholders:
- `$counter`: will be replaced with the number of issues, or the format defined in the `overflow-format` option.

#### `header-prs`
Shown when there are PRs to show, acts as section title for the PRs.

Possible values: Any `string`

##### Placeholders:
- `$counter`: will be replaced with the number of PRs, or the format defined in the `overflow-format` option.

#### `overflow-format`
This is going to be used to replace the `$counter` in the `header-issues` and the `header-prs` options when there are more issues or PRs to show than the allowed maximum.

Possible values: Any `string`

##### Placeholders:
- `$showing`: will be replaced with the number of items shown.
- `$from`: will be replaced with the number of items found in total.

#### `line-prs`
Format to use for each PR found.

Possible values: Any `string`

##### Placeholders:
- `$login`: will be replaced with the author's username.
- `$mergeableEmoji`: will be replaced with an emoji depicting the mergeability status (red/yellow/green circle).
- `$title`: will be replaced with the title of the PR.
- `$repository`: will be replaced with the repository's name in which the PR was found.
- `$age`: will be replaced with the time passed since the creation of the PR.
- `$deletions`: will be replaced with the number of lines removed.
- `$additions`: will be replaced with the number of lines added.
- `$link`: will be replaced with an HTTP url leading to the PR.
- `$assignee-logins`: will be replaced with a comma separated list of GitHub login names.
- `$assignee-login-links`: will be replaced with a comma separated list of GitHub login names linking to the GitHub profile pages.
- `$button`: will be replaced with a button leading to the PR, this option is available only when the `mode` option is set to `block`.

#### `line-issues`
Format to use for each issue found.

Possible values: Any `string`

##### Placeholders:
- `$login`: will be replaced with the author's username.
- `$title`: will be replaced with the title of the issue.
- `$repository`: will be replaced with the repository's name in which the issue was found.
- `$age`: will be replaced with the time passed since the creation of the issue.
- `$link`: will be replaced with an HTTP url leading to the issue.
- `$assignee-logins`: will be replaced with a comma separated list of GitHub login names from assigned reviewers on an Issue or PR.
- `$assignee-login-links`: will be replaced with a comma separated list of GitHub login names linking to the GitHub profile pages from assigned reviewers on an Issue or PR.
- `$reviewer-logins`: will be replaced with a comma separated list of GitHub login names from requested reviewers on a PR.
- `$reviewer-login-links`: will be replaced with a comma separated list of GitHub login names linking to the GitHub profile pages from assigned reviewers on a PR.
- `$button`: will be replaced with a button leading to the issue, this option is available on when the `mode` option is set to `block`.

#### `no-results`
Shown only when there are no Issues or PRs to be shown. 

Possible values: Any `string`

#### `skip-no-results`
Controls whether the `no-results` text is shown or not. With the default settings the `no-results` message will be displayed.

Possible values: `true`, `false`

Default: `false`

#### `delete-old-messages`
When `delete-old-messages` is `true`, previous notifications will be found based on the Slack channel and the Slack Bot's username and then deleted.
To fully enable this feature, the [`SLACK_API_USER_TOKEN`](https://github.com/bbaga/github-scheduled-reminder-app#slack_api_user_token) has to be provided as well. 

##### Required scopes
`SLACK_API_TOKEN`: `chat:write`, `users:read` 

`SLACK_API_USER_TOKEN`: `search:read` 

Possible values: `true`, `false`

Default: `false`
