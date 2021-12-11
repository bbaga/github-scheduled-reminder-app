# Persistent state storage

## What is the persistent state storage?
The application maintains an internal state (or configuration) that includes the schedules and information about the monitored repositories, this state is kept in memory and is lost when the application has to restart.

To avoid a lengthy bootstrap process and waiting for the application to scan for the configurations in the repositories, it is possible to configure a persistent file storage where the state will be dumped periodically, and on restart the application will read back the state from these files.

The persistent storage modes can be configured through environment variables further discussed below.

## `STATE_STORAGE_TYPE`
Storage type, at this time it can be local file system, GCS Bucket implementation is coming soon.

**Required**: No
**Default**: `LOCAL_FS`
**Options:**
- `LOCAL_FS`: Local file system
- `GCS_BUCKET`: Google Cloud Storage (GCS) Bucket

## Persistent state storage on local file system
When `LOCAL_FS` is used in `STATE_STORAGE_TYPE`.

### `STATE_STORAGE_FS_FILEPATH`
The application's state will be stored at this location on local disk.

**Required**: No
**Default**: `/tmp/github-reminder-application.state.json`

### Example
#### Configuring the local FS storage
```bash
export STATE_STORAGE_TYPE=LOCAL_FS
export STATE_STORAGE_FS_FILEPATH=/tmp/github-reminder-application.state.json
```

## Persistent state storage in a Google Cloud Storage (GCS) Bucket
When `GCS_BUCKET` is used in `STATE_STORAGE_TYPE`.

### `STATE_STORAGE_GCS_BUCKET_NAME`
GCS bucket name.

**Required**: Yes

### `STATE_STORAGE_GCS_BUCKET_SECRET`
Required when `STATE_STORAGE_GCS_BUCKET_SECRET_FILE` isn't set. Secret for the service account that can read and write to the bucket.

**Required**: Yes

### `STATE_STORAGE_GCS_BUCKET_SECRET_FILE`
Required when `STATE_STORAGE_GCS_BUCKET_SECRET` isn't set. Location of the file containing the secret for the service account that can read and write to the bucket.

**Required**: Yes

### `STATE_STORAGE_GCS_BUCKET_FILEPATH`
File path to the state file in the GCS bucket.

**Required**: No
**Default**: `application.state.json`

### Example
#### Configuring the Google Cloud Storage (GCS) Bucket storage
```bash
export STATE_STORAGE_TYPE=GCS_BUCKET
export STATE_STORAGE_GCS_BUCKET_NAME=my-gcs-bucket
export STATE_STORAGE_GCS_BUCKET_SECRET_FILE=/secure/location/credentials.json
export STATE_STORAGE_GCS_BUCKET_FILEPATH=application.state.json
```
