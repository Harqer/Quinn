# Meta Wearables DAT SDK

> Full API reference: https://wearables.developer.meta.com/llms.txt?full=true
> DAT docs MCP: https://mcp.developer.meta.com/wearables
> Developer docs: https://wearables.developer.meta.com/docs/develop/

## Code style

## Quick Reference

| Task | Command |
|------|---------|
| Build app | `./gradlew assembleDebug` |
| Run tests | `./gradlew test` |
| Install app | `./gradlew installDebug` |
| Lint app | `./gradlew lint` |

## Architecture

The SDK is organized into four public modules:

- **mwdat-core**: Registration, permissions, devices, and session creation
- **mwdat-camera**: Stream capability, video frames, and photo capture
- **mwdat-display**: Display capability, display UI components, icons, images, buttons, and video
- **mwdat-mockdevice**: MockDeviceKit for testing without hardware

### Initialization and session setup

```kotlin
Wearables.initialize(context)

val session = Wearables.createSession(AutoDeviceSelector()).getOrElse { error ->
    throw IllegalStateException(error.description)
}
session.start()

val stream = session.addStream(StreamConfiguration()).getOrElse { error ->
    throw IllegalStateException(error.description)
}
stream.start().getOrElse { error ->
    throw IllegalStateException(error.description)
}
```

## Kotlin patterns

- Use `DatResult<T, E>` for typed success and failure handling
- Observe state with `StateFlow` and `Flow`
- Create a `Session` first, then attach capabilities such as `Stream` or `Display`
- Keep frame handling off the main thread when doing heavier processing

## Error handling

```kotlin
Wearables.checkPermissionStatus(Permission.CAMERA)
    .onSuccess { status -> /* handle status */ }
    .onFailure { error, _ -> /* handle error */ }
```

Avoid `getOrThrow()` in user-facing samples. Surface typed errors from `DatResult` instead.

## Naming conventions

| Type | Purpose | Example |
|------|---------|---------|
| `Session` | Device connection lifecycle | `Wearables.createSession(...)` |
| `Stream` | Camera capability on a session | `session.addStream(...)` |
| `Display` | Display capability on a session | `session.addDisplay(...)` |
| `*Selector` | Device targeting | `AutoDeviceSelector` |
| `*Error` | Typed failure surface | `SessionError`, `StreamError` |

## Key types

- `Wearables` — SDK entry point
- `Session` — lifecycle for an interaction with a linked device
- `Stream` — camera capability attached to a session
- `Display` — display capability attached to a session
- `StreamConfiguration` — video quality and frame rate configuration
- `MockDeviceKit` — simulated device environment for testing

## Live docs search

If your editor supports remote MCP servers, connect `https://mcp.developer.meta.com/wearables` and use `search_dat_docs` for current DAT setup, session lifecycle, camera streaming, MockDeviceKit, permissions, and exact API symbols. This public docs server does not require authentication; do not configure tokens, OAuth, or custom authorization headers for it.

Use `llms.txt` when your tool only supports static reference context.

## Testing with MockDeviceKit

```kotlin
val mockDeviceKit = MockDeviceKit.getInstance(context)
mockDeviceKit.enable()
val device = mockDeviceKit.pairGlasses(GlassesModel.RAYBAN_META).getOrThrow()
```

Use MockDeviceKit to drive registration, device availability, streaming media, and permission scenarios without physical hardware.

## Common pitfalls

- Do not call SDK APIs before `Wearables.initialize(context)`
- Do not assume a session implies streaming or display access; capabilities are attached separately
- Do not ignore `DatResult` failures from `createSession`, `start`, `addStream`, `addDisplay`, or `capturePhoto`
- Do not reuse terminally stopped sessions

## Links

- [Android API reference](https://wearables.developer.meta.com/docs/reference/android/dat/0.8)
- [Developer documentation](https://wearables.developer.meta.com/docs/develop/)
- [GitHub repository](https://github.com/facebook/meta-wearables-dat-android)

## Dev environment tips

Set up the Meta Wearables Device Access Toolkit in an Android app.

## Prerequisites

- Android Studio Flamingo or newer
- Android 10+ test device with the Meta AI app installed
- Supported Meta glasses or MockDeviceKit for local testing
- Developer Mode enabled in the Meta AI app for development builds
- GitHub personal access token with `read:packages` scope

## Step 1: Add the Maven repository

In `settings.gradle.kts`:

```kotlin
val localProperties =
    Properties().apply {
        val localPropertiesPath = rootDir.toPath() / "local.properties"
        if (localPropertiesPath.exists()) {
            load(localPropertiesPath.inputStream())
        }
    }

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/facebook/meta-wearables-dat-android")
            credentials {
                username = ""
                password = System.getenv("GITHUB_TOKEN") ?: localProperties.getProperty("github_token")
            }
        }
    }
}
```

## Step 2: Declare dependencies

In `libs.versions.toml`:

```toml
[versions]
mwdat = "0.8.0"

[libraries]
mwdat-core = { group = "com.meta.wearable", name = "mwdat-core", version.ref = "mwdat" }
mwdat-camera = { group = "com.meta.wearable", name = "mwdat-camera", version.ref = "mwdat" }
mwdat-display = { group = "com.meta.wearable", name = "mwdat-display", version.ref = "mwdat" }
mwdat-mockdevice = { group = "com.meta.wearable", name = "mwdat-mockdevice", version.ref = "mwdat" }
```

In `app/build.gradle.kts`:

```kotlin
android {
    defaultConfig {
        manifestPlaceholders["mwdat_application_id"] = "0"
        manifestPlaceholders["mwdat_client_token"] = "0"
    }
}

dependencies {
    implementation(libs.mwdat.core)
    implementation(libs.mwdat.camera)
    implementation(libs.mwdat.display)
    implementation(libs.mwdat.mockdevice)
}
```

## Step 3: Configure `AndroidManifest.xml`

```xml
<manifest ...>
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
    <uses-permission android:name="android.permission.INTERNET" />

    <application ...>
        <meta-data
            android:name="com.meta.wearable.mwdat.APPLICATION_ID"
            android:value="${mwdat_application_id}" />
        <meta-data
            android:name="com.meta.wearable.mwdat.CLIENT_TOKEN"
            android:value="${mwdat_client_token}" />

        <activity android:name=".MainActivity" ...>
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data android:scheme="myexampleapp" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

`APPLICATION_ID` and `CLIENT_TOKEN` are used for app attestation and can be found in the Wearables Developer Center. In Developer Mode, attestation is not used, so the manifest placeholders can both be `0`. For production, replace both placeholders with the credentials for your Wearables Developer Center app. Replace `myexampleapp` with your app's URL scheme.

## Step 4: Initialize the SDK

```kotlin
import com.meta.wearable.dat.core.Wearables

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Wearables.initialize(this)
            .onFailure { error, _ -> error("Failed to initialize DAT: ${error.description}") }
    }
}
```

## Step 5: Register and create a session

```kotlin
import com.meta.wearable.dat.core.Wearables
import com.meta.wearable.dat.core.selectors.AutoDeviceSelector

fun connect(activity: Activity) {
    Wearables.startRegistration(activity)
}

fun startSession() {
    val session = Wearables.createSession(AutoDeviceSelector()).getOrElse { error ->
        throw IllegalStateException(error.description)
    }

    session.start()
}
```

Observe registration and available devices:

```kotlin
lifecycleScope.launch {
    Wearables.registrationState.collect { state ->
        // Update registration UI
    }
}

lifecycleScope.launch {
    Wearables.devices.collect { devices ->
        // Update the device list
    }
}
```

## Step 6: Add camera streaming

```kotlin
import com.meta.wearable.dat.camera.addStream
import com.meta.wearable.dat.camera.types.StreamConfiguration
import com.meta.wearable.dat.camera.types.VideoQuality

val stream = session.addStream(
    StreamConfiguration(videoQuality = VideoQuality.MEDIUM, frameRate = 24),
).getOrElse { error ->
    throw IllegalStateException(error.description)
}

stream.start().onFailure { error, _ ->
    throw IllegalStateException(error.description)
}
```

## Next steps

- [Camera Streaming](camera-streaming.md) — Stream capability, video frames, photo capture
- [MockDevice Testing](mockdevice-testing.md) — Test without hardware
- [Session Lifecycle](session-lifecycle.md) — Handle session and stream state changes
- [Permissions](permissions-registration.md) — Registration and permission flows
- [Full Android API reference](https://wearables.developer.meta.com/docs/reference/android/dat/0.8)

## Testing instructions

Use MockDeviceKit to test DAT SDK integrations without physical Meta glasses.

MockDeviceKit simulates Meta glasses behavior for development and testing. It provides:
- `MockDeviceKit` — Entry point for creating simulated devices
- `MockGlasses` — Simulated Ray-Ban Meta glasses
- `MockCameraKit` — Simulated camera with configurable video feed and photo capture

## Setup

Add `mwdat-mockdevice` to your Gradle dependencies:

```kotlin
dependencies {
    implementation(libs.mwdat.mockdevice)
}
```

## Creating a mock device

```kotlin
import com.meta.wearable.dat.mockdevice.MockDeviceKit
import com.meta.wearable.dat.mockdevice.api.MockDeviceKitConfig

val mockDeviceKit = MockDeviceKit.getInstance(context)

// Attach fake registration and connectivity (auto-initializes Wearables if needed).
// By default, Wearables.registrationState transitions to Registered.
mockDeviceKit.enable()

// Or start in unregistered state to test registration flows:
// mockDeviceKit.enable(MockDeviceKitConfig(initiallyRegistered = false))

val device = mockDeviceKit.pairGlasses(GlassesModel.RAYBAN_META).getOrThrow()
```

You can check `mockDeviceKit.isEnabled` to query whether the mock environment is active.

## Simulating device states

```kotlin
// Simulate glasses lifecycle
device.powerOn()
device.unfold()
device.don()    // Simulate wearing the glasses

// Later...
device.doff()   // Simulate removing
device.fold()
device.powerOff()
```

## Configuring permissions

MockDeviceKit provides `permissions` to control permission behavior without the Meta AI app.

By default, `RequestPermissionContract` returns `Granted`. Use `set()` to control `checkPermissionStatus()` and `setRequestResult()` to control request outcomes.

```kotlin
val mockDeviceKit = MockDeviceKit.getInstance(context)

// Simulate denied camera permission status
mockDeviceKit.permissions.set(Permission.CAMERA, PermissionStatus.Denied)

// Simulate denied request result (user tapping "deny")
mockDeviceKit.permissions.setRequestResult(Permission.CAMERA, PermissionStatus.Denied)
```

## Setting up mock camera feeds

### Video streaming

```kotlin
val camera = device.services.camera
camera.setCameraFeed(videoUri)
```

### Photo capture

```kotlin
val camera = device.services.camera
camera.setCapturedImage(imageUri)
```

**Note**: Android doesn't transcode video automatically. Mock video files must be in h.265 format. Use FFmpeg to convert:

```bash
ffmpeg -hwaccel videotoolbox -i input.mp4 -c:v hevc_videotoolbox -c:a aac_at -tag:v hvc1 -vf "scale=540:960" output.mov
```

## Writing instrumentation tests

Create a reusable test base class:

```kotlin
import android.content.Context
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.meta.wearable.dat.mockdevice.MockDeviceKit
import com.meta.wearable.dat.mockdevice.api.MockDeviceKitInterface
import org.junit.After
import org.junit.Before
import org.junit.Rule

open class MockDeviceKitTestCase<T : Any>(
    private val activityClass: Class<T>
) {
    @get:Rule
    val scenarioRule = ActivityScenarioRule(activityClass)

    protected lateinit var mockDeviceKit: MockDeviceKitInterface
    protected lateinit var targetContext: Context

    @Before
    open fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        targetContext = instrumentation.targetContext
        mockDeviceKit = MockDeviceKit.getInstance(targetContext)
        grantRuntimePermissions()
    }

    @After
    open fun tearDown() {
        mockDeviceKit.disable()
    }

    private fun grantRuntimePermissions() {
        val packageName = targetContext.packageName
        val shell = InstrumentationRegistry.getInstrumentation().uiAutomation
        shell.executeShellCommand("pm grant $packageName android.permission.BLUETOOTH_CONNECT")
        shell.executeShellCommand("pm grant $packageName android.permission.CAMERA")
    }
}
```

## Using MockDeviceKit in the CameraAccess sample

The CameraAccess sample app includes a Debug menu for MockDeviceKit:

1. Tap the **Debug icon** to open the MockDeviceKit menu
2. Tap **Pair RayBan Meta** to create a simulated device
3. Use **PowerOn**, **Unfold**, **Don** to simulate glasses states
4. Select video/image files for mock camera feeds
5. Start streaming to see simulated frames

## Supported media formats

| Type | Formats |
|------|---------|
| Video | h.264 (AVC), h.265 (HEVC) |
| Image | JPEG, PNG |

## Links

- [Mock Device Kit overview](https://wearables.developer.meta.com/docs/mock-device-kit)
- [Android testing guide](https://wearables.developer.meta.com/docs/testing-mdk-android)

## Building and streaming

Use a `Session` and attached `Stream` to receive frames and capture photos.

## Key concepts

- **Session**: Device connection lifecycle created through `Wearables.createSession(...)`
- **Stream**: Camera capability attached to a session with `session.addStream(...)`
- **StreamConfiguration**: Resolution and frame rate configuration for the stream
- **PhotoData**: Still image captured from glasses while streaming

## Create a session and attach a stream

```kotlin
import com.meta.wearable.dat.camera.Stream
import com.meta.wearable.dat.camera.addStream
import com.meta.wearable.dat.camera.types.StreamConfiguration
import com.meta.wearable.dat.camera.types.VideoQuality
import com.meta.wearable.dat.core.Wearables
import com.meta.wearable.dat.core.selectors.AutoDeviceSelector

val session = Wearables.createSession(AutoDeviceSelector()).getOrElse { error ->
    throw IllegalStateException(error.description)
}
session.start()

val stream: Stream = session.addStream(
    StreamConfiguration(
        videoQuality = VideoQuality.MEDIUM,
        frameRate = 24,
    ),
).getOrElse { error ->
    throw IllegalStateException(error.description)
}

stream.start().getOrElse { error ->
    throw IllegalStateException(error.description)
}
```

### Resolution options

| Quality | Size |
|---------|------|
| `VideoQuality.HIGH` | 720 x 1280 |
| `VideoQuality.MEDIUM` | 504 x 896 |
| `VideoQuality.LOW` | 360 x 640 |

### Frame rate options

Valid values: `2`, `7`, `15`, `24`, `30` FPS.

Lower resolution and frame rate usually produce better visual quality per frame over Bluetooth.

## Observe stream state

`StreamState` transitions: `STOPPED` -> `STARTING` -> `STARTED` -> `STREAMING` -> `STOPPING` -> `STOPPED` -> `CLOSED`

```kotlin
lifecycleScope.launch {
    stream.state.collect { state ->
        when (state) {
            StreamState.STREAMING -> {
                // Frames are flowing
            }
            StreamState.STOPPED -> {
                // Streaming ended
            }
            StreamState.CLOSED -> {
                // Stream fully closed
            }
            else -> Unit
        }
    }
}
```

## Receive frames

```kotlin
lifecycleScope.launch {
    stream.videoStream.collect { frame ->
        updatePreview(frame)
    }
}
```

## Capture a photo

```kotlin
lifecycleScope.launch {
    stream.capturePhoto()
        .onSuccess { photoData ->
            val imageBytes = photoData.data
            savePhoto(imageBytes)
        }
        .onFailure { error, _ ->
            showCaptureError(error.description)
        }
}
```

## Clean up

Stop the stream when you no longer need camera data, then stop the parent session if the device interaction is finished.

```kotlin
stream.stop()
session.stop()
```

If you want to remove the capability entirely before re-adding it, call `session.removeStream()`.

## Links

- [Android API reference](https://wearables.developer.meta.com/docs/reference/android/dat/0.8)
- [Integration guide](https://wearables.developer.meta.com/docs/build-integration-android)

## Session management

Manage session and stream state in DAT SDK integrations.

Create a `Session` with `Wearables.createSession(...)`, start it, then attach capabilities such as camera streaming. Session lifecycle and stream lifecycle are related but distinct.

## Session states

| State | Meaning | App action |
|-------|---------|------------|
| `IDLE` | Session created, not started yet | Call `session.start()` |
| `STARTING` | Connecting to the device | Show loading UI |
| `STARTED` | Session active and ready for capabilities | Add or use capabilities |
| `PAUSED` | Session temporarily suspended | Keep state, wait for resume or stop |
| `STOPPING` | Session is shutting down | Stop user work and wait |
| `STOPPED` | Session ended | Release resources and create a new session if needed |

## Observe session state

```kotlin
val session = Wearables.createSession(AutoDeviceSelector()).getOrElse { error ->
    throw IllegalStateException(error.description)
}
session.start()

lifecycleScope.launch {
    session.state.collect { state ->
        when (state) {
            DeviceSessionState.STARTED -> onStarted()
            DeviceSessionState.PAUSED -> onPaused()
            DeviceSessionState.STOPPED -> onStopped()
            else -> Unit
        }
    }
}
```

## Stream state

Camera streaming has its own state flow after you attach a stream:

```text
STOPPED -> STARTING -> STARTED -> STREAMING -> STOPPING -> STOPPED -> CLOSED
```

```kotlin
lifecycleScope.launch {
    stream.state.collect { state ->
        // React to camera capability state changes
    }
}
```

## Common transitions

The SDK may pause or stop a session when:

- Another experience takes over the device
- The user removes or folds the glasses
- Bluetooth connectivity drops
- The user unregisters the app or revokes needed access

## Pause and resume

When a session is paused:

- The device connection may remain active
- Attached capabilities stop doing useful work
- Your app should wait for the next observed session state instead of trying to force a restart

## Device availability

```kotlin
lifecycleScope.launch {
    Wearables.devices.collect { devices ->
        // Update the list of available devices
    }
}
```

Use `Wearables.devices` and device metadata to decide when it is sensible to create a new session after a stop.

## Checklist

- [ ] Handle all `DeviceSessionState` values you care about
- [ ] Observe stream state separately from session state
- [ ] Release resources only after stop or close
- [ ] Recreate sessions after terminal stops instead of reusing dead ones
- [ ] Surface typed `SessionError` and `StreamError` failures

## Links

- [Session lifecycle documentation](https://wearables.developer.meta.com/docs/lifecycle-events)
- [Android API reference](https://wearables.developer.meta.com/docs/reference/android/dat/0.8)

## Permissions

Register your app with Meta AI, then request the device permissions it needs.

The DAT SDK separates two steps:

1. **Registration**: The user connects your app to Meta AI.
2. **Device permissions**: After registration, your app requests capabilities such as camera access.

Both flows depend on the Meta AI app being installed on the phone.

## Start registration

```kotlin
Wearables.startRegistration(activity)
```

Observe registration state:

```kotlin
lifecycleScope.launch {
    Wearables.registrationState.collect { state ->
        // Update your registration UI
    }
}
```

To unregister:

```kotlin
Wearables.startUnregistration(activity)
```

## Check permission status

`checkPermissionStatus(...)` is a suspend API that returns a `DatResult`.

```kotlin
lifecycleScope.launch {
    Wearables.checkPermissionStatus(Permission.CAMERA)
        .onSuccess { status ->
            if (status == PermissionStatus.Granted) {
                startStreaming()
            }
        }
        .onFailure { error, _ ->
            showPermissionError(error.description)
        }
}
```

## Request a permission

Use `Wearables.RequestPermissionContract()` with the Activity Result API:

```kotlin
private val permissionLauncher =
    registerForActivityResult(Wearables.RequestPermissionContract()) { result ->
        result.onSuccess { status ->
            if (status == PermissionStatus.Granted) {
                startStreaming()
            }
        }.onFailure { error, _ ->
            showPermissionError(error.description)
        }
    }

fun requestCameraPermission() {
    permissionLauncher.launch(Permission.CAMERA)
}
```

Users can allow once or allow always through the Meta AI flow.

## Developer Mode vs production

| Mode | Registration behavior |
|------|------------------------|
| Developer Mode | Use `mwdat_application_id = 0` and `mwdat_client_token = 0` manifest placeholders for local development |
| Production | Use the application ID and client token assigned in the Wearables Developer Center |

For development builds, enable Developer Mode in the Meta AI app before testing registration and permissions.

## Prerequisites

- Internet connection for registration
- Meta AI app installed on the phone
- Callback URI scheme configured in `AndroidManifest.xml`
- Bluetooth permission granted on Android

## Links

- [Permissions documentation](https://wearables.developer.meta.com/docs/permissions-requests)
- [Manage projects](https://wearables.developer.meta.com/docs/manage-projects)
- [Android integration guide](https://wearables.developer.meta.com/docs/build-integration-android)

## Debugging

Diagnose common setup, session, and stream issues in DAT SDK integrations.

## Quick diagnosis

```text
No eligible device or session won't start?
|
+-- Did you call Wearables.initialize(context)? -> Must happen before SDK usage
|
+-- Did registration complete? -> Observe Wearables.registrationState
|
+-- Is Developer Mode enabled? -> Enable it in the Meta AI app for dev builds
|
+-- Does Wearables.devices contain a linked device? -> Check Bluetooth and range
|
+-- Did createSession() or addStream() return a DatResult failure? -> Surface the typed error
```

## Developer Mode

Developer Mode must be enabled for local development builds that use `mwdat_application_id = 0` and `mwdat_client_token = 0`.

### Symptoms when Developer Mode is disabled

- Registration flow completes but the device never becomes eligible
- Permission requests do not succeed for development builds
- `Wearables.createSession(...)` fails with no eligible device

### Watch for

- Developer Mode may reset after app or firmware updates
- Developer Mode is configured per linked device
- Production builds use a real `APPLICATION_ID`, `CLIENT_TOKEN`, and release-channel gating instead

## Session and stream issues

### Session never reaches `STARTED`

- Verify `Wearables.registrationState`
- Check that `Wearables.devices` contains a compatible linked device
- Ensure the glasses are powered on, unfolded, and in range

### Stream never reaches `STREAMING`

- Confirm `session.start()` succeeded before calling `session.addStream(...)`
- Check camera permission status through `Wearables.checkPermissionStatus(...)`
- Make sure `stream.start()` returned success

### Photo capture fails

- `capturePhoto()` only succeeds while the stream is actively streaming
- Surface the returned `CaptureError` instead of discarding the `DatResult`

## Version compatibility

Ensure compatible versions of the SDK, Meta AI app, and glasses firmware. See [version dependencies](https://wearables.developer.meta.com/docs/version-dependencies) for the current compatibility matrix.

## Logging

```kotlin
private const val TAG = "DATWearables"

stream.start()
    .onFailure { error, _ -> Log.e(TAG, "Failed to start stream: ${error.description}") }
```

Prefer logging typed `DatResult` failures and observed state transitions over generic exceptions.

## Checklist

- [ ] `Wearables.initialize(context)` ran before SDK usage
- [ ] Developer Mode enabled for development builds
- [ ] `APPLICATION_ID` and `CLIENT_TOKEN` match the build mode
- [ ] Registration completed before session creation
- [ ] Bluetooth permission granted
- [ ] Camera permission granted through Meta AI
- [ ] Session and stream `DatResult` failures are surfaced in logs or UI

## Links

- [Known issues](https://wearables.developer.meta.com/docs/knownissues)
- [Version dependencies](https://wearables.developer.meta.com/docs/version-dependencies)
- [Troubleshooting discussions](https://github.com/facebook/meta-wearables-dat-android/discussions)

## Sample app

Build an Android DAT app with registration, sessions, camera streaming, and photo capture.

Pair this with the [CameraAccess sample](https://github.com/facebook/meta-wearables-dat-android/tree/main/samples).

## Project setup

1. Create an Android Studio app project.
2. Add the DAT Maven repository and dependencies.
3. Configure `AndroidManifest.xml` for registration callbacks plus `APPLICATION_ID` and `CLIENT_TOKEN`.
4. Initialize `Wearables` in your `Application`.

## Suggested app structure

```text
app/src/main/java/com/example/myapp/
├── MyApplication.kt
├── MainActivity.kt
├── session/
│   └── SessionViewModel.kt
└── ui/
    ├── RegistrationScreen.kt
    └── CameraScreen.kt
```

## Registration and session creation

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            Wearables.registrationState.collect { state ->
                // Update registration UI
            }
        }
    }

    fun register() {
        Wearables.startRegistration(this)
    }
}
```

```kotlin
class SessionViewModel : ViewModel() {
    private var session: Session? = null
    private var stream: Stream? = null

    fun startCameraSession() {
        val createdSession = Wearables.createSession(AutoDeviceSelector()).getOrElse { error ->
            throw IllegalStateException(error.description)
        }
        createdSession.start()
        session = createdSession

        stream = createdSession.addStream(
            StreamConfiguration(videoQuality = VideoQuality.MEDIUM, frameRate = 24),
        ).getOrElse { error ->
            throw IllegalStateException(error.description)
        }.also { addedStream ->
            addedStream.start().getOrElse { error ->
                throw IllegalStateException(error.description)
            }
        }
    }
}
```

## Observe frames and capture photos

```kotlin
viewModelScope.launch {
    stream?.videoStream?.collect { frame ->
        // Render preview
    }
}

fun capturePhoto() {
    viewModelScope.launch {
        stream?.capturePhoto()
            ?.onSuccess { photoData ->
                savePhoto(photoData.data)
            }
            ?.onFailure { error, _ ->
                showCaptureError(error.description)
            }
    }
}
```

## Shutdown

```kotlin
fun stopCameraSession() {
    stream?.stop()
    session?.stop()
    stream = null
    session = null
}
```

## Testing with MockDeviceKit

Use `MockDeviceKit` to simulate linking glasses, permission state, and camera media without physical hardware. See [MockDevice Testing](mockdevice-testing.md) for setup details.

## Links

- [CameraAccess sample](https://github.com/facebook/meta-wearables-dat-android/tree/main/samples)
- [Android integration guide](https://wearables.developer.meta.com/docs/build-integration-android)
- [Developer documentation](https://wearables.developer.meta.com/docs/develop/)

## Display Access

Add `mwdat-display` when rendering content on Meta Ray-Ban Display glasses. Display apps also need the core DAT setup from getting-started and permissions-registration: initialize DAT once, complete registration, request Bluetooth and Internet permissions, configure DAT manifest metadata, and set `com.meta.wearable.mwdat.DAM_ENABLED` to `true`.

```toml
mwdat-display = { group = "com.meta.wearable", name = "mwdat-display", version.ref = "mwdat" }
```

```kotlin
dependencies {
    implementation(libs.mwdat.core)
    implementation(libs.mwdat.display)
}
```

Set `mwdat_application_id` and `mwdat_client_token` from manifest placeholders or `local.properties`, as in the DisplayAccess sample. Request runtime permissions before `Wearables.initialize(context)`. Observe `Wearables.registrationState` and `Wearables.registrationErrorStream`; wait for `RegistrationState.REGISTERED` before creating a display session.

For a picker, collect `Wearables.devices` and per-device `Wearables.devicesMetadata[id]`. Show the device name, `device.deviceType.description`, `device.linkState`, `device.compatibility`, and `device.isDisplayCapable()`. Enable selection only for connected display-capable devices, surface `DeviceCompatibility.DEVICE_UPDATE_REQUIRED` with `Wearables.openFirmwareUpdate(activity)`, and use `SpecificDeviceSelector(selectedDeviceId)` for the selected row. Use `AutoDeviceSelector(filter = { it.isDisplayCapable() })` only when automatic selection is acceptable.

Attach Display only after the `DeviceSession` reaches `STARTED`, and enable user content only once the Display capability reaches `DisplayState.STARTED`. Observe `session.errors`; `DeviceSession.start()` returns `Unit`. If session creation or `session.errors` reports `DeviceSessionError.DAT_APP_ON_THE_GLASSES_UPDATE_REQUIRED`, show an update action that calls `Wearables.openDATGlassesAppUpdate(activity)`.

```kotlin
import com.meta.wearable.dat.core.Wearables
import com.meta.wearable.dat.core.selectors.SpecificDeviceSelector
import com.meta.wearable.dat.core.session.DeviceSessionState
import com.meta.wearable.dat.core.types.DeviceIdentifier
import com.meta.wearable.dat.core.types.DeviceSessionError
import com.meta.wearable.dat.core.types.RegistrationState
import com.meta.wearable.dat.display.Display
import com.meta.wearable.dat.display.addDisplay
import com.meta.wearable.dat.display.types.DisplayState
import com.meta.wearable.dat.display.views.ButtonStyle
import com.meta.wearable.dat.display.views.FlexBoxBackground
import com.meta.wearable.dat.display.views.IconName
import com.meta.wearable.dat.display.views.TextStyle

fun startDisplaySession(selectedDeviceId: DeviceIdentifier) {
    if (Wearables.registrationState.value != RegistrationState.REGISTERED) {
        showError("Register with Meta AI before starting Display")
        return
    }

    val session =
        Wearables.createSession(SpecificDeviceSelector(selectedDeviceId)).fold(
            onSuccess = { it },
            onFailure = { error, _ ->
                if (error == DeviceSessionError.DAT_APP_ON_THE_GLASSES_UPDATE_REQUIRED) {
                    showDatAppUpdateAction()
                }
                showError(error.description)
                return
            },
        )
    var display: Display? = null

    lifecycleScope.launch {
        session.errors.collect { error ->
            if (error == DeviceSessionError.DAT_APP_ON_THE_GLASSES_UPDATE_REQUIRED) {
                showDatAppUpdateAction()
            }
            showError(error.description)
        }
    }

    lifecycleScope.launch {
        session.state.collect { state ->
            if (state == DeviceSessionState.STARTED && display == null) {
                session.addDisplay()
                    .onSuccess { newDisplay ->
                        display = newDisplay
                        lifecycleScope.launch {
                            newDisplay.state.collect { displayState ->
                                setTryItEnabled(displayState == DisplayState.STARTED)
                                if (displayState == DisplayState.STARTED) {
                                    newDisplay.sendContent {
                                        flexBox(
                                            gap = 12,
                                            padding = 24,
                                            background = FlexBoxBackground.CARD,
                                        ) {
                                            text("Bike ride", style = TextStyle.HEADING)
                                            button(
                                                label = "Done",
                                                style = ButtonStyle.PRIMARY,
                                                iconName = IconName.CHECKMARK,
                                                onClick = { showDoneState() },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    .onFailure { error, _ -> showError(error.description) }
            }
        }
    }
    session.start()
}
```

Build exactly one root view per `sendContent` call: use a root `flexBox { ... }` for UI, or a root `video(player = player)` for video. Do not put `video(...)` inside a `flexBox`. Button and clickable `flexBox` callbacks are routed back to the phone app; keep callbacks fast and delegate to app state or ViewModel methods. Use `IconName` enum values such as `IconName.GEAR`, not raw strings.

For URL video, create `VideoPlayer(source = VideoSource.Url(...), codec = VideoCodec.MP4)`, send it with `display.sendContent { video(player = player) }`, and call `player.play()` after send success. Collect `player.state` and `player.error`; on `VideoPlayerState.ENDED`, cancel the video observer and send the next display screen. On cleanup, cancel state/error collection jobs, close or replace active video players, call `session.removeDisplay()`, then stop the session.

---

# JavaScript/TypeScript Coding Standards
*Personal project standard — last updated July 2026*

> Philosophy: consistency beats cleverness. These rules exist so you don't have to re-decide the same thing every time you open the editor. If a rule ever fights the goal of shipping working code, the goal wins — update the doc instead of arguing with yourself.

---

## 1. Tooling (set once, then forget)

| Tool | Purpose | Config |
|---|---|---|
| **TypeScript** | Type safety | `strict: true` in `tsconfig.json`, no exceptions |
| **ESLint** | Linting | `@typescript-eslint/recommended` + `eslint-plugin-import` |
| **Prettier** | Formatting | Defaults, 2-space indent, semicolons on, single quotes |
| **Vitest / Jest** | Testing | Pick one, don't mix |
| **Husky + lint-staged** | Pre-commit hooks | Run lint + format on staged files only |

**Rule:** Formatting is never a judgment call — Prettier decides, you don't argue with it. If Prettier and your instinct disagree, Prettier wins; change the config once if it's really wrong, don't hand-fix output.

---

## 2. TypeScript specifics

- **`strict: true`** always. No `any` unless it's genuinely unknown external data — and even then, prefer `unknown` + narrowing.
- **Types vs Interfaces:** use `interface` for object shapes that might be extended (props, entities); use `type` for unions, tuples, and utility compositions. Don't agonize past that.
- **No implicit `any`** — if TS can't infer it, annotate it.
- **Avoid `enum`** — prefer `as const` object maps or union string literals; they're more tree-shakeable and easier to serialize.
- **Null vs undefined:** pick `undefined` for "not set yet" and reserve `null` for "explicitly empty" (e.g., API returned no value). Don't use both interchangeably.
- **Non-null assertion (`!`)** is a last resort, not a shortcut — if you need it more than rarely, the types are wrong somewhere upstream.

---

## 3. Naming conventions

| What | Convention | Example |
|---|---|---|
| Variables, functions | `camelCase` | `getUserById` |
| Classes, types, interfaces | `PascalCase` | `UserProfile` |
| Constants (true constants) | `UPPER_SNAKE_CASE` | `MAX_RETRIES` |
| Files (non-component) | `kebab-case.ts` | `user-service.ts` |
| React components | `PascalCase.tsx` | `UserCard.tsx` |
| Booleans | prefix `is/has/should/can` | `isLoading`, `hasError` |
| Private class members | `#field` (real private) | `#cache` |

**Rule of thumb:** name things for what they *are*, not how they're implemented. `activeUsers` not `filteredArr2`.

---

## 4. File & folder structure

```
src/
  features/            # group by feature, not by file type
    auth/
      auth.service.ts
      auth.types.ts
      login-form.tsx
      auth.test.ts
    users/
  shared/
    components/
    hooks/
    utils/
  lib/                  # third-party wrappers/config (e.g. api client)
```

- **Colocate tests** next to the file they test (`thing.ts` + `thing.test.ts`), not in a mirrored `__tests__` tree.
- **One default export per file max**, and prefer named exports overall — easier to refactor and grep.
- **Barrel files (`index.ts`)** only at feature boundaries, not everywhere — they slow down builds and obscure import paths if overused.

---

## 5. Function & code style

- **Functions do one thing.** If you need "and" to describe it, split it.
- **Prefer pure functions** where possible — same input, same output, no hidden state mutation.
- **Early returns over nested conditionals:**
  ```ts
  // Good
  function process(user?: User) {
    if (!user) return null;
    if (!user.isActive) return null;
    return doWork(user);
  }
  ```
- **Arrow functions** for callbacks/inline logic; **named function declarations** for top-level functions (better stack traces, hoisting is fine here).
- **No magic numbers/strings** — extract to a named constant if it means something.
- **Max function length ~40 lines** as a smell-detector, not a hard rule — if you blow past it, ask whether it should be two functions.

---

## 6. Async & error handling

- **Always `async/await`** over raw `.then()` chains.
- **Never swallow errors silently.** A bare `catch {}` is a bug waiting to happen — at minimum log it, ideally handle or rethrow with context.
- **Custom error classes** for domain errors so callers can `instanceof` check:
  ```ts
  class NotFoundError extends Error {
    constructor(resource: string) {
      super(`${resource} not found`);
      this.name = 'NotFoundError';
    }
  }
  ```
- **Wrap external calls** (fetch, DB, third-party SDKs) in a try/catch at the boundary — don't let raw network errors leak into UI code.

---

## 7. Imports

Order, top to bottom, blank line between groups:
1. Node/external packages (`react`, `zod`, etc.)
2. Internal absolute imports (`@/features/...`)
3. Relative imports (`./`, `../`)
4. Types (can mix in or use `import type` explicitly — be consistent)

Use `import type { Foo } from './foo'` for type-only imports so they're erased at build time.

---

## 8. Comments & documentation

- **Comment the *why*, not the *what*.** Code already says what it does; comments explain the non-obvious reasoning, trade-off, or gotcha.
- **JSDoc on exported functions** that aren't self-explanatory from name + types alone — skip it for trivial getters.
- **No commented-out code** left in commits — delete it, git remembers.
- **TODO comments** must include context: `// TODO(you): revisit once API v2 ships pagination`

---

## 9. Git & commits

- **Conventional commits:** `feat:`, `fix:`, `refactor:`, `chore:`, `docs:`, `test:`
- **One logical change per commit.** Small, revertible, readable in `git log`.
- **Present tense, imperative mood:** `fix login redirect bug`, not `fixed` or `fixes`.
- **Never commit** `.env`, secrets, or `node_modules` — `.gitignore` set up front.

---

## 10. Testing

- **Test behavior, not implementation.** If a refactor with the same output breaks your tests, the tests were too coupled.
- **Naming:** `describe('UserService')` → `it('throws NotFoundError when user does not exist')` — reads like a sentence.
- **Coverage isn't the goal** — meaningful coverage of edge cases and error paths is. 100% coverage of trivial getters is wasted effort.
- **Mock at the boundary** (network, filesystem, time) — not your own internal logic.

---

## 11. Quick pre-commit checklist

- [ ] `tsc --noEmit` passes with no errors
- [ ] ESLint passes with no warnings ignored
- [ ] Prettier formatted (should be automatic via hook)
- [ ] No `console.log` left in (use a real logger or remove)
- [ ] No commented-out code
- [ ] Tests pass locally
- [ ] Commit message follows convention

---

## 12. When to break these rules

These are defaults, not laws. Break a rule when:
- Following it would make the code *harder* to understand, not easier
- You're prototyping and will clean up before merging (say so in the commit)
- A third-party library forces a different pattern

Write a one-line comment explaining the deviation so future-you isn't confused.

---

# Production Readiness Checklist
*Android • Firebase • Gemini Lyria • Meta Wearables (camera + real-time voice) • Play Store*

> This assumes the app is built and working. The goal here is finding what breaks under real users, real cost, and real Play Store review — not code style. Go section by section; each has a "why this matters for your specific app" note because your stack (continuous camera + mic + AI generation) has failure modes a typical CRUD app doesn't.

---

## 1. Security

**Why it matters more for you:** you're streaming camera frames and live audio off someone's face. That's a materially higher trust bar than most apps.

- [ ] **No API keys in the client.** Gemini Lyria key, any Meta/third-party keys — none of them should be bundled in the APK. Route all Gemini calls through a Cloud Function / Cloud Run proxy that holds the key server-side.
- [ ] **Firebase App Check enabled** — prevents unauthorized clients (scraped API endpoints, bots) from hitting your Cloud Functions or Firestore directly.
- [ ] **Firestore/RTDB security rules reviewed line-by-line** — default-deny, then explicit allow per collection. Test rules with the Firebase emulator, not just "it works from my app."
- [ ] **Auth tokens refreshed properly** — verify `onIdTokenChanged` (not just `onAuthStateChanged`) so expired tokens don't silently fail mid-session during a long voice call.
- [ ] **Camera/mic data in transit is encrypted** (WSS/TLS for any WebRTC signaling or socket connection — never plain `ws://`).
- [ ] **No raw camera frames or audio persisted to Firestore/Storage unless required** — if you only need frames transiently for the "vibe" inference, don't store them. If you do store them (e.g., for regenerating a track later), they need explicit retention limits and user-facing deletion controls.
- [ ] **PII minimization** — if the camera can capture bystanders, you need a stated policy (Play Store will ask about this — see §9).
- [ ] **Rate limit Cloud Function endpoints** per user/device to prevent abuse driving up your Gemini bill (see §6).
- [ ] **Meta Wearables permission flow uses "Allow once" vs "Allow always" correctly** — don't request persistent access if the feature only needs it during an active session.
- [ ] **Secrets in Cloud Functions use Firebase Secret Manager**, not `.env` files committed anywhere or plaintext `functions.config()`.

---

## 2. Database schema (Firestore/RTDB)

**Why it matters more for you:** real-time voice + live camera inference generates a *lot* of write/read volume fast. Bad schema = bad bill and bad latency simultaneously.

- [ ] **No unbounded arrays in documents** (e.g., a growing list of every "vibe" or session event inside a user doc) — Firestore docs cap at 1MB and large docs get slower to read/write. Use a subcollection instead.
- [ ] **Avoid hot documents** — if many clients write to the same doc concurrently (e.g., a shared session/room doc updated on every frame), you'll hit contention. Shard or move high-frequency fields to a subcollection/RTDB path instead.
- [ ] **Composite indexes created for every query you actually run** — check the Firebase console for "missing index" errors in production logs, not just local dev.
- [ ] **Real-time listeners are scoped tightly** — don't listen to a whole collection when you need one doc; every listener update is a live cost and bandwidth line item.
- [ ] **Session/vibe history modeled as its own collection** with a `userId` + `createdAt` index, not nested inside the user document.
- [ ] **RTDB vs Firestore choice matches the workload** — RTDB is typically better for very high-frequency ephemeral data (live voice state, presence); Firestore better for structured, queryable history (past generated tracks, user library). If you're using one for both, double check it's the right fit.
- [ ] **TTL / cleanup policy** for ephemeral session data — Firestore TTL policies or a scheduled Cloud Function to purge old session/camera-derived data.
- [ ] **Backups configured** (scheduled Firestore export to Cloud Storage) in case of accidental bulk delete or bad migration.

---

## 3. Real-time voice & camera streaming

**Why this is its own section:** this is the part most likely to fall over under real network conditions (not your wifi).

- [ ] **Reconnect logic tested** — what happens when a user walks out of wifi range mid-voice-session? Does the session resume, or silently die?
- [ ] **Jitter buffer / backpressure handling** for audio — verify behavior on 3G/weak LTE, not just good wifi.
- [ ] **Meta Wearables session lifecycle handled for all states** — glasses folded, glasses powered off mid-stream, Bluetooth disconnect. Don't hardcode an assumed reason for a `STOPPED` state; check the actual state transition.
- [ ] **Battery impact tested** — continuous camera streaming + real-time voice + AI generation is heavy; measure actual battery drain per session length on a real Android device, not emulator.
- [ ] **Background/foreground service behavior** — Android increasingly restricts background mic/camera access. Verify your foreground service notification is correctly declared if audio/camera continues when the app isn't focused (required for Play Store compliance, not optional).
- [ ] **Graceful degradation** if Gemini Lyria is slow/unavailable — does the user get a "generating..." state or does the app appear frozen?

---

## 4. Caching

**Why it matters more for you:** Gemini Lyria generation is presumably not free or instant — regenerating the same "vibe" twice is wasted latency and wasted money.

- [ ] **Cache generated tracks by input signature** (e.g., hash of the camera-derived "vibe" descriptor + params) so an identical input doesn't re-trigger generation.
- [ ] **CDN/Firebase Hosting cache headers set correctly** for any static assets (album art, UI assets) — long cache + cache-busting filenames, not no-cache everywhere.
- [ ] **Client-side memory cache for recently generated tracks** during a session, so switching between "vibes" the user already generated doesn't re-hit the network.
- [ ] **Cache invalidation policy is explicit** — if a user regenerates or edits a vibe, make sure stale cached audio isn't served back to them.
- [ ] **Firestore read caching** — for data unlikely to change often (user preferences, static config), use `get()` with cache-first settings rather than always paying for a live listener.

---

## 5. Batching

**Why it matters more for you:** camera-derived inference and voice data naturally arrive as a stream — sending every frame/chunk as its own API call is both slow and expensive.

- [ ] **Camera frames batched/throttled before inference**, not sent at full framerate — decide the minimum sampling rate that still produces good "vibes" (e.g., 1 frame every N seconds) and cap it.
- [ ] **Firestore writes batched** using `WriteBatch` for any multi-document update (e.g., logging a session + updating usage counters) instead of separate calls.
- [ ] **Analytics/usage events batched and flushed periodically**, not fired individually per frame or per voice chunk.
- [ ] **Gemini API calls batched where the API supports it**, and otherwise debounced so rapid-fire camera changes don't trigger a generation call per frame.

---

## 6. Cost effectiveness

**Why it matters more for you:** this is the one that quietly kills solo/indie AI apps — a viral moment with unthrottled Gemini + Firebase calls can produce a bill nobody planned for.

- [ ] **Per-user and per-day quota/rate limits** on Gemini Lyria generation calls — enforced server-side (Cloud Function), not just client-side (which is trivially bypassed).
- [ ] **Budget alerts set in Google Cloud/Firebase console** at multiple thresholds (e.g., 50%, 90%, 100% of expected monthly spend) — don't rely on discovering it via the invoice.
- [ ] **Firestore read/write cost modeled** — estimate cost per active session (listeners + writes) × expected DAU, not just per-document cost.
- [ ] **Cloud Functions cold start / invocation cost checked** — if a function fires per camera frame, that's a very different cost profile than per-session.
- [ ] **Egress bandwidth accounted for** — audio streaming both directions adds up; check Firebase/GCP egress pricing against expected session length × DAU.
- [ ] **Free tier vs paid tier usage tracked separately** if you're using Firebase Spark plan features anywhere — know your actual headroom before launch, not after.
- [ ] **Fallback/degraded mode defined** for when quotas are hit (e.g., "generation limit reached today" rather than the app silently failing or the bill silently climbing).

---

## 7. Observability & monitoring

- [ ] **Crashlytics (or equivalent) integrated** and verified to actually receive test crashes before launch.
- [ ] **Structured logging** on Cloud Functions (not just `console.log`) so you can query by session ID / user ID when debugging a specific bad session.
- [ ] **Alerting on Gemini API error rate** — if the third-party API degrades, you want to know before your users start complaining.
- [ ] **Alerting on cost anomalies** (see §6) tied to actual notification (email/Slack), not just a dashboard nobody checks.
- [ ] **Session success rate tracked** (voice sessions that complete vs. drop) as a real product metric, not just crash-free rate.

---

## 8. Security/permissions review specific to Meta Wearables

- [ ] **Production registration completed** with the Meta AI app (not just Developer Mode) — confirm `APPLICATION_ID` is set correctly for release builds, or your release APK will fail to connect to glasses for real users.
- [ ] **Permission rationale strings are clear and honest** — Android will show your camera/mic permission prompt; vague rationale increases both user distrust and Play Store review friction.
- [ ] **Tested with Mock Device Kit AND real hardware** before submission — simulator-only testing misses real Bluetooth/connection edge cases.

---

## 9. Play Store submission readiness

**This is the section most likely to get you rejected or delayed if skipped** — camera + microphone + AI-generated content together put you in a higher-scrutiny review bucket.

- [ ] **Privacy Policy published and linked** — mandatory, and must specifically address camera and microphone data use given your feature set (not a generic boilerplate policy).
- [ ] **Data Safety form completed accurately** in Play Console — declare camera, microphone, and any data shared with third parties (Google/Gemini). Mismatches between declared and actual behavior are a common rejection reason.
- [ ] **Sensitive permissions justified** in Play Console's permissions declaration form — camera + microphone + background service all require explicit justification text.
- [ ] **Target API level meets Play Store's current minimum** (check Play Console for the current requirement — this changes yearly, verify at submission time, not from memory).
- [ ] **Content rating questionnaire completed** — AI-generated audio content may need review for whether generated output could include unexpected content; consider if any content moderation is needed on generated output.
- [ ] **Bystander privacy addressed** — since the camera may capture people other than the user, consider whether you need on-device processing (frames never leave device) vs. cloud processing, and disclose accordingly. This is both a legal and review-risk issue.
- [ ] **Foreground service declaration matches actual behavior** if camera/mic continue when app is backgrounded — Play Store checks this against your manifest and will reject mismatches.
- [ ] **Tested on a range of real Android devices/OS versions**, not just your dev device — Bluetooth/glasses pairing behavior varies meaningfully across OEMs.
- [ ] **Closed testing track run first** (internal → closed → open) rather than going straight to production, especially given hardware-dependent (glasses) functionality.
- [ ] **Crash-free rate and ANR rate meet Play Console's health thresholds** before requesting full rollout — Play Store increasingly gates visibility/rollout speed on this.
- [ ] **Staged rollout percentage set** (e.g., 5% → 20% → 50% → 100%) rather than 100% on day one, so a bad build doesn't hit your whole user base at once.

---

## 10. Final go/no-go checklist

Run through this the day before submission:

- [ ] All of §1 (Security) checked
- [ ] Cost alerts are live and tested (§6) — send yourself a test alert
- [ ] Privacy Policy + Data Safety form are consistent with each other and with actual app behavior (§9)
- [ ] At least one full real-device test: fresh install → glasses pairing → live voice session → music generation → app backgrounded mid-session → resumed
- [ ] Staged rollout configured, not 100% release
- [ ] Rollback plan exists (previous APK version ready to re-promote if the new release regresses)
- [ ] Someone other than you has used the app once, cold, with no guidance

---

*Note: Play Store policy details (target API level, Data Safety form specifics, permission requirements) change periodically. Worth a quick check of the current Play Console requirements at submission time rather than relying on this doc if it's been a while.*