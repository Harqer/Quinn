# Implementation Plan: High-Fidelity Testing Strategy (v15.0)

This plan establishes a comprehensive testing strategy for Mave Studio, covering unit, UI, and screenshot tests to ensure the reliability of the AI orchestration engine and the Studio HUD.

## Current Testing Setup Audit

- **DI Framework**: Hilt (Production & Testing).
- **Unit Testing**: JUnit4 with Mockk.
- **Platform Simulation**: Robolectric.
- **UI Framework**: 100% Jetpack Compose.
- **UI Testing**: Compose UI Test APIs + Espresso Core.
- **Coverage**: Jacoco (Plugin applied).

## Proposed Changes

### 1. Infrastructure: Screenshot & E2E Tools

#### [MODIFY] [libs.versions.toml](file:///home/shaolin/lyria/gradle/libs.versions.toml)
- Add **Roborazzi** for local, high-speed screenshot testing of Compose components.
- Add **UI Automator** for end-to-end testing of system-level interactions (e.g., permission dialogs, credential manager).

#### [MODIFY] [build.gradle.kts](file:///home/shaolin/lyria/app/build.gradle.kts)
- Apply the Roborazzi plugin.
- Configure `testOptions` for Robolectric and Roborazzi.

---

### 2. Test Harness: Fakes & Mocks

#### [NEW] [FakeApiClient.kt](file:///home/shaolin/lyria/app/src/test/java/com/musically/studio/fakes/FakeApiClient.kt)
- A non-networked implementation of `ApiClient` to test `MainViewModel` without hitting the backend.

#### [NEW] [MaveTestRunner.kt](file:///home/shaolin/lyria/app/src/androidTest/java/com/musically/studio/MaveTestRunner.kt)
- Custom Hilt test runner for instrumented tests.

---

### 3. Test Suites: Sovereignty Verification

#### [NEW] [MainViewModelTest.kt](file:///home/shaolin/lyria/app/src/test/java/com/musically/studio/ui/MainViewModelTest.kt)
- **Unit Test**: Verify state transitions for track playback, recording, and verified credential flow.

#### [NEW] [HomeScreenTest.kt](file:///home/shaolin/lyria/app/src/test/java/com/musically/studio/ui/screens/HomeScreenTest.kt)
- **Robolectric UI Test**: Verify that the "Strike a Vibe" button triggers the correct ViewModel state.

#### [NEW] [MaveScreenshotTest.kt](file:///home/shaolin/lyria/app/src/test/java/com/musically/studio/ui/MaveScreenshotTest.kt)
- **Roborazzi Test**: Capture baseline screenshots of the Studio HUD in different window sizes (Phone vs. Tablet).

---

### 4. Code Coverage

- **Task**: Configure Jacoco to generate merged reports for both local and instrumented tests.

## Verification Plan

### Automated Tests
- Run unit tests: `./gradlew :app:testDebugUnitTest`
- Run screenshot capture: `./gradlew recordRoborazziDebug`
- Run instrumented tests: `./gradlew :app:connectedDebugAndroidTest`

### Manual Verification
- Review the generated HTML coverage report in `app/build/reports/jacoco`.
- Inspect the Roborazzi `screenshots` directory for visual regressions.
