# Testing Strategy Analysis: Mave Studio

## 1. Dependency Injection Framework
- **Production**: Hilt (`com.google.dagger:hilt-android:2.60.1`)
- **Testing**: Hilt Testing (`com.google.dagger:hilt-android-testing:2.60.1`)

## 2. Unit Testing Framework
- **Local**: JUnit 4
- **Mocking**: Mockk (`io.mockk:mockk:1.14.11`)
- **Coroutines**: `kotlinx-coroutines-test:1.11.0`

## 3. Platform Simulation
- **Robolectric**: Enabled in `build.gradle.kts` for local UI and platform-dependent tests. Used with `GraphicsMode.NATIVE`.

## 4. UI Framework
- **Composition**: 100% Jetpack Compose.
- **Testing**: Compose UI Testing (`androidx.compose.ui:ui-test-junit4`)

## 5. Screenshot Testing
- **Framework**: Roborazzi (`io.github.takahirom.roborazzi`)
- **Execution**: Local JVM via Robolectric.

## 6. End-to-End (E2E) Testing
- **Framework**: UI Automator (`androidx.test.uiautomator:uiautomator:2.3.0`)
- **Targets**: Android Credential Manager, system permission dialogs.

## 7. Current Coverage & Gaps
- **ViewModel**: `MainViewModelTest.kt` established (Local).
- **Network**: `RealNetworkIntegrationTest.kt` (Local/Robolectric) and `FakeApiClient.kt` established.
- **UI**: `HomeScreenTest.kt` established (Local/Robolectric).
- **Screenshots**: `MaveScreenshotTest.kt` established.
- **Gaps**:
    - Navigation logic tests (Backstack, Deep links).
    - E2E journey for Credential Manager.
    - Wearable session lifecycle tests.
    - Comprehensive UI behavior tests for Onboarding.

***

**Next Steps**: Implement Navigation tests and a critical path E2E test for the Identity flow.
