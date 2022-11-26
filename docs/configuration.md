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
Certain Slack resources require user authentication. You can learn more about the user token [here](https://api.slack.com/authentication/token-types)

**Required**: No

### `SLACK_API_USER_TOKEN_FILE`
Certain Slack resources require user authentication. You can learn more about the user token [here](https://api.slack.com/authentication/token-types)

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

## Example configuration file
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
