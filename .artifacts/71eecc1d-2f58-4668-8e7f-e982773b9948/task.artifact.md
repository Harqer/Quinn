# Task: High-Fidelity Testing Strategy (v15.0)

- `[ ]` Infrastructure: Screenshot & E2E Tools
    - `[ ]` Add Roborazzi and UI Automator to `libs.versions.toml`
    - `[ ]` Apply Roborazzi plugin and configure `build.gradle.kts`
- `[ ]` Test Harness: Fakes & Mocks
    - `[ ]` Create `FakeApiClient.kt`
    - `[ ]` Create `MaveTestRunner.kt` for Hilt instrumented tests
- `[ ]` Test Suites: Sovereignty Verification
    - `[ ]` Create `MainViewModelTest.kt` (Unit)
    - `[ ]` Create `HomeScreenTest.kt` (UI/Robolectric)
    - `[ ]` Create `MaveScreenshotTest.kt` (Roborazzi)
- `[ ]` Code Coverage
    - `[ ]` Finalize Jacoco merge configuration
- `[ ]` Verification
    - `[ ]` Run unit tests
    - `[ ]` Record Roborazzi screenshots
