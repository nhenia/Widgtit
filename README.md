# Hint Widget

Hint Widget is a simple Android application that provides a home screen widget displaying random helpful hints.

## Features

- **Android Widget**: A resizable home screen widget.
- **Kotlin-based**: Built using modern Android development practices with Kotlin.
- **Automatic Refresh**: The widget refreshes its content whenever the device is unlocked (`ACTION_USER_PRESENT`).
- **Auto-sizing Text**: Uses Android's auto-sizing TextView to ensure hints fit the widget regardless of its size.

## Building the Project

To build the project, run:
```bash
./gradlew assembleDebug
```

## Running Tests

To run unit tests, run:
```bash
./gradlew test
```
