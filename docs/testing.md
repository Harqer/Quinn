# Testing Strategy

This document outlines the testing strategy for the Mave application.

## Test Infrastructure
- **Dependency Injection**: We use Hilt (`@HiltAndroidTest`).
- **Mocking**: `mockk` for Android and JVM, plus `Mockito` for Java dependencies.
- **Fakes**: In-memory fakes are located in `com.musically.studio.network` in both `test` and `androidTest` to decouple UI tests from actual servers and WebSocket connections (e.g., `FakeMaveSessionManager`).

## 1. Unit Tests (JVM)
Unit tests verify view models, repositories, and domain logic without involving Android components.
**Location**: `app/src/test/java/com/musically/studio/...`
**Run**: `./gradlew testDebugUnitTest`

## 2. UI Behavior Tests (Local JVM via Robolectric)
We use Robolectric to run Compose UI tests directly on the JVM, allowing fast UI logic and interaction verification without an emulator.
**Location**: `app/src/test/java/com/musically/studio/ui/...`
**Run**: `./gradlew testDebugUnitTest`

## 3. Local Screenshot Tests (Roborazzi)
Screenshot tests catch visual regressions. We use Roborazzi for local screenshot tests.
**Location**: `app/src/test/java/com/musically/studio/ui/...ScreenshotTest.kt`
**Record Baselines**: `./gradlew recordRoborazziDebug`
**Verify**: `./gradlew verifyRoborazziDebug`

## 3. End-to-End (E2E) Testing (Automated)

Instead of maintaining custom Kotlin UIAutomator/Espresso scripts, we use third-party E2E automation tools to ensure reliability and minimize boilerplate.

### A. Firebase Test Lab (Robo Test)
We use **Firebase Robo Test** for zero-code, comprehensive UI crawling.
- **What it does:** Automatically installs the app on physical devices in the cloud, clicks every button, fills out forms, and tries to crash the app.
- **How to run:** 
  1. Build your APK: `./gradlew assembleDebug`
  2. Upload to Firebase Test Lab via the Firebase Console or `gcloud` CLI.
  3. No scripts needed—just let the crawler explore.

### B. Maestro (YAML UI Flows)
We use [Maestro](https://maestro.mobile.dev/) for deterministic, human-readable E2E flows.
- **Location:** `.maestro/`
- **What it does:** Runs specific user journeys (e.g., "Tap Library", "Assert Playlist Visible") using simple YAML files.
- **How to run locally:**
  1. Install Maestro: `curl -Ls "https://get.maestro.mobile.dev" | bash`
  2. Boot an Android Emulator.
  3. Run the flow: `maestro test .maestro/flow.yaml`

## Continuous Integration (CI)
All PRs must pass `./gradlew testDebugUnitTest` and `./gradlew verifyRoborazziDebug`.
