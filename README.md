# github-scheduled-reminder-app
Scheduled reminders about PRs and Issues

⚠ This is all experimental work, not meant to provide any value at this stage!


# ToC
1. [Development](#development)
2. [Configuration](#configuration) 

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

#Configuration

## Complete example
```yaml
# .demo-bot.yaml in the bbaga/app-testing repository

enabled: true
notifications:
  - name: slack-notification
    schedule: "*/30 * * * * ?"
    type: slack/channel
    config:
      channel: "test-channel"
  
  - extending: 
      repository: bbaga/app-testing
      name: slack-notification
```

| Field | Is Required? | Description |
|---|---|---|
| `enabled` | No | When this is `true`, the configuration file will be processed, otherwise ignored. |
| `notifications` | No | This field should contain a list of [`Notification`](#notification-objects) objects, these objects can be just pointers to other objects. In the example above, the first entry represents a schedule configuration, the second entry tell the application to set up notifications for this repository based on the Notification object called `slack-notification` in the `bbaga/app-testing`. Notification objects can reference schedules in other repositories. |

## Notification objects

### Schedules
| Field | Is Required? | Description |
|---|---|---|
| `name` | Yes | This name can be used to reference the object from other repositories as well. |
| `schedule` | Yes | Cron schedule pattern that supports seconds as well, first position is the seconds. More on the format [here](http://www.quartz-scheduler.org/documentation/quartz-2.3.0/tutorials/crontrigger.html#format). |
| `type` | Yes | The only supported `type` is `slack/channel`. |
| `config` | No | Depends on the `type` field's value, each notification type may have different configuration. |

### References
| Field | Is Required? | Description |
|---|---|---|
| `extending` | Yes | With this field we can tell the application that we want to use an already existing schedule. |
| `extending.repository` | Yes | In which repository will the application find the schedule we are trying to use. |
| `extending.name` | Yes | What is the name of the schedule we are trying to use. |
