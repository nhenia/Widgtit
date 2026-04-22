# Widgtit

A simple Android widget that provides daily hints.

## Features
- Automatic updates when the user is present.
- Resizable widget support.
- Configurable hints.
- Batched Updates: Widget updates and Room database inserts are performed in batches to minimize overhead.

## Build
The app uses a custom versioning system.
- `MAJOR` and `MINOR` versions are defined in `version.properties`.
- `PATCH` is automatically incremented based on the build environment.

## GitHub Actions
Every push to `main` or `master` triggers a build and tags the repository with `MAJOR.MINOR`.
