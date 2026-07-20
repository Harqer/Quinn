# Task: Finalizing Mave Studio Frontend (No More Mocks)

- `[x]` MainViewModel: State & Logic Completion
    - `[x]` Implement missing playback and UI states
    - `[x]` Implement user auth methods (Guest & Email)
    - `[x]` Implement full playback control suite
- `[x]` Studio Core: Real-Time Multimodal
    - `[x]` Implement production-grade frame analyzer in `CameraPreview.kt`
    - `[x]` Refactor `MainActivity.kt` for "Audio-First" guest routing
- `[x]` UI Polish & Interaction
    - `[x]` Connect `NowPlayingScreen.kt` slider and duration labels
    - `[x]` Fix `TrackItems.kt` album art placeholders
    - `[x]` Implement "More Options" bottom sheet in `AlbumViewScreen.kt`
    - `[x]` Connect `DevicesScreen.kt` to real `WearableStreamingService` lifecycle
- `[x]` Verification
    - `[x]` Grep audit for "Mock" or "TODO" in production paths
    - `[x]` Verify guest session audio generation
    - `[x]` Final push to main
