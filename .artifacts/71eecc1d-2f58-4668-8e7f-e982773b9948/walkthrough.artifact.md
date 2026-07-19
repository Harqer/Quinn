# Walkthrough: Android Build & APK Generation

I have successfully resolved the Android build errors and generated the debug APK for testing.

## Key Accomplishments

### 1. "File Name Too Long" Fix
- **Build Relocation**: Addressed the `FileSystemException: File name too long` error by relocating the Gradle build directory to `/tmp/lyria-build`. This shortens the absolute paths for generated synthetic classes (lambdas), allowing the build to complete on Linux environments with path length restrictions.
- **Persistence**: Updated the root `build.gradle.kts` with this configuration so that future builds remain stable.

### 2. APK Generation
- **Successful Build**: Ran `./gradlew :app:assembleDebug` and verified the build completed successfully in ~5 minutes.
- **Output**: The debug APK has been extracted and placed in the project root for your convenience.

## Verification Results

### Build Artifacts
- [x] **APK Location**: [musically-debug.apk](file:///home/shaolin/lyria/musically-debug.apk)
- [x] **File Size**: ~103 MB
- [x] **Build Type**: Debug (Testing-ready)

> [!IMPORTANT]
> **GitHub File Size Limit**: The APK file exceeds GitHub's 100MB limit. I have placed it in the project root for you to copy to your phone, but it has **not** been pushed to the remote repository to avoid push failures.

> [!TIP]
> To test the app, simply transfer the `musically-debug.apk` file to your Android device and install it. Ensure your phone's "Live POV" features are accessible for Quinn to start creating music!
