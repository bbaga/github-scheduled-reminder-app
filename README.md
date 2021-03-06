[![Java CI with Maven](https://github.com/bbaga/github-scheduled-reminder-app/actions/workflows/maven.yml/badge.svg)](https://github.com/bbaga/github-scheduled-reminder-app/actions/workflows/maven.yml?query=branch%3Amain)
[![CodeQL](https://github.com/bbaga/github-scheduled-reminder-app/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/bbaga/github-scheduled-reminder-app/actions/workflows/codeql-analysis.yml?query=branch%3Amain)

# github-scheduled-reminder-app
Scheduled reminders about PRs and Issues. This project may be useful for teams using GitHub Enterprise versions that doesn't have [the built-in scheduled reminder](https://docs.github.com/en/organizations/organizing-members-into-teams/managing-scheduled-reminders-for-your-team) feature yet. 

> ⚠ This tool comes with no guaranties, use it with a healthy dose of caution!

## Example GitHub PR and Issue reminder on Slack:
![image](./docs/images/slack-example.png "GitHub PR and Issue reminder on Slack")

# ToC
1. [Development](#development)
2. [Configuration](#configuration)
   1. [Sources](docs/sources.md)
   2. [Template configuration options](docs/template-config.md)
   3. [Persistent State Storage](docs/persistent-state-storage.md)

# Development
## Requirements
You'll need [docker](https://www.docker.com/products/docker-desktop) to follow the steps in this guide.

## Configuring the environment
First, make a copy of the `.env.dist` file and name it `.env` in the same directory. We will store some configuration values in this file.

## Getting the necessary certificates with other authentication details
### GitHub
To run this application, you will need a GitHub application that can read the details of the pull requests and issues as well as has read access for the `.demo-bot.yaml` path in the repositories.

#### App ID
To find the App ID you have to navigate to your application's settings page, you can see the applications available to you [here](https://github.com/settings/apps). The App ID will be visible in the _About_ section, on the top.

Copy the App ID and paste it into the `.env` file after `GITHUB_ID=`, on the same line.

#### App certificate
You have to have a GitHub application and [generate a private key](https://docs.github.com/en/developers/apps/building-github-apps/authenticating-with-github-apps#generating-a-private-key).
Once the private key is available, it has to be converted from `pkcs1` to `pkcs8`, you can do that with `openssl`:
```bash
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in pkcs1.key -out pkcs8.key
```

Now copy the contents of the `pkcs8.key` file into the `.env` file, between double quotes ("..."), on the same line after `GITHUB_APP_CERT=`

### Slack
You have to create a Slack application, install it on your work space with permissions to write on channels, then navigate to the _Install App_ page, where you should find the _OAuth Tokens for Your Workspace_ section with the tokens.

Copy the appropriate one to the `.env` file, after the `SLACK_API_TOKEN=`, on the same line.

## Contents of the .env file
After the steps above, your `.env` file should look similar to this example:

```
GITHUB_ID=12345
GITHUB_APP_CERT="-----BEGIN PRIVATE KEY----- XXXXYYYYZZZZ -----END PRIVATE KEY-----"
SLACK_API_TOKEN=xoxb-XXXX-YYYY-ZZZZ
```

## Starting the application
To start the application, execute the following command:
```bash
docker-compose up app
```

If the application doesn't work at this point, please open an issue.

# Configuration

## Environment variables

### `GITHUB_ID`
The GitHub application id as per the [App ID](#app-id) section.

**Required**: Yes

### `GITHUB_APP_CERT`
Mandatory when `GITHUB_APP_CERT_FILE` isn't set. Contents of the github application certificate in `pkcs8` format as per the [App certificate](#app-certificate) section.

**Required**: Yes

### `GITHUB_APP_CERT_FILE`
Mandatory when `GITHUB_APP_CERT` isn't set. Location of the file containing the github application certificate in `pkcs8` format as per the [App certificate](#app-certificate) section.

**Required**: Yes

### `GITHUB_API_ENDPOINT`
The GitHub API endpoint in case it is a custom installation.

**Required**: No

### `SLACK_API_TOKEN`
Mandatory when `SLACK_API_TOKEN_FILE` isn't set. Slack API token as per the [Slack](#slack) section.

**Required**: Yes

### `SLACK_API_TOKEN_FILE`
Mandatory when `SLACK_API_TOKEN` isn't set. Location of the file containing the Slack API token as per the [Slack](#slack) section.

**Required**: Yes

### `SLACK_API_USER_TOKEN`
Certain Slack resources require user authentication. The user token is necessary to enable the old notification removal.
You can learn more about the user token [here](https://api.slack.com/authentication/token-types)

**Required**: No

### `SLACK_API_USER_TOKEN_FILE`
Certain Slack resources require user authentication. The user token is necessary to enable the old notification removal.
You can learn more about the user token [here](https://api.slack.com/authentication/token-types)

**Required**: No

### `JOBS_GITHUB_INSTALLATION_SCAN_INTERVAL`
This is the interval in milliseconds between installation scans and consequently repository scans for configuration updates.

**Required**: No
**Default**: 43200000

### `ACTIVITY_TRACKING_ENABLED`
Enables tracking clicks on the links/buttons posted with the notifications.

**Required**: No
**Default**: `false`

### `ACTIVITY_TRACKING_ENDPOINT_URL`
The application has to know its own endpoint to build tracking urls that will forward users to the target urls.

**Required**: No
**Default**: `http://localhost:8080`

## Complete example
```yaml
# .demo-bot.yaml in the bbaga/app-testing repository

enabled: true
notifications:
  - name: slack-notification
    type: slack/scheduled/channel
    config:
      schedule: "*/30 * * * * ?"
      timezone: "EST"
      channel: "test-channel"
      repositories:
        bbaga/app-testing:
          sources: ... # Sources config is optional. See more at docs/sources.md 
```

| Field | Is Required? | Description |
|---|---|---|
| `enabled` | No | When this is `true`, the configuration file will be processed, otherwise ignored. |
| `notifications` | No | This field should contain a list of [`Notification`](#notification-objects) objects, these objects can be just pointers to other objects. In the example above, the first entry represents a schedule configuration, the second entry tell the application to set up notifications for this repository based on the Notification object called `slack-notification` in the `bbaga/app-testing`. Notification objects can reference schedules in other repositories. |

## Notification objects
| Field | Is Required? | Description |
|---|---|---|
| `name` | Yes | This name can be used to reference the object from other repositories as well. |
| `type` | Yes | The only supported `type` is `slack/scheduled/channel`. |
| `config` | No | Depends on the `type` field's value, each notification type may have different configuration. |

### Config
| Field | Is Required? | Description |
|---|---|---|
| `schedule` | Yes | Cron schedule pattern that supports seconds as well, first position is the seconds. More on the format [here](http://www.quartz-scheduler.org/documentation/quartz-2.3.0/tutorials/crontrigger.html#format). |
| `timezone` | No | Timezone to adjust the schedule to. Defaults to `UTC`. |
| `repositories` | No | Map of repositories to configuration values. |

## References
| Field | Is Required? | Description |
|---|---|---|
| `extending` | Yes | With this field we can tell the application that we want to use an already existing schedule. |
| `extending.repository` | Yes | In which repository will the application find the schedule we are trying to use. |
| `extending.name` | Yes | What is the name of the schedule we are trying to use. |
