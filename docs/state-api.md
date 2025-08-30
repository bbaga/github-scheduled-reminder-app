# State API

## Overview
The State API provides endpoints to interact with the application's internal state and configuration. It allows you to view the current configuration state, check the configuration buffer, and trigger updates for specific repositories.

## Endpoints

### Get Configuration State
Retrieves the current configuration state of the application.

**Endpoint**: `GET /state/config`

**Response**: Returns a JSON object containing the current configuration graph nodes.

**Example**:
```bash
curl -X GET http://localhost:8080/state/config
```

### Get Configuration Buffer
Retrieves the configuration buffer, which contains repository records that are being processed but not yet committed to the main configuration.

**Endpoint**: `GET /state/config/buffer`

**Response**: Returns a JSON object containing the configuration buffer with repository records.

**Example**:
```bash
curl -X GET http://localhost:8080/state/config/buffer
```

### Update Repository Configuration
Triggers an update for a specific repository's configuration. This endpoint will fetch the configuration from the repository and update the application's state accordingly.

**Endpoint**: `GET /state/update/repo/{org}/{repository}`

**Path Parameters**:
- `org`: The GitHub organization or user name
- `repository`: The repository name

**Response**: Returns a status message indicating the result of the update operation.
- "OK" if the update was successful
- "Repository is archived or the config is disabled" if the repository is archived or the configuration is disabled
- "Something went wrong, please check the logs." if an error occurred during the update

**Example**:
```bash
curl -X GET http://localhost:8080/state/update/repo/bbaga/github-scheduled-reminder-app
```

## How It Works
The State API interacts with the application's internal state management system:

1. The configuration state is stored in memory as a graph of `ConfigGraphNode` objects.
2. When updating a repository's configuration, the API:
   - Authenticates with GitHub using the installation token
   - Fetches the repository configuration
   - Processes the configuration and updates the internal state
   - Clears outdated configuration entries

## Use Cases
- **Monitoring**: Check the current state of the application's configuration
- **Debugging**: View the configuration buffer to understand what changes are being processed
- **Manual Updates**: Trigger configuration updates for specific repositories without waiting for the scheduled scan