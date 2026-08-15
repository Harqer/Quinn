# Code Quality, Logic & Complexity Checklist

## 1. Correctness & Concurrency
- **Thread Safety & Race Conditions**: Ensure shared mutable state accessed across threads or coroutine scopes is protected via `Mutex`, thread-safe data structures (`ConcurrentHashMap`, atomic primitives), or actor patterns.
- **Resource Lifecycle & Leaks**: Always release system resources (`AudioRecord`, `AudioTrack`, `MediaController`, `WebSocket` connections, temporary `/tmp` files) upon scope cancellation, activity destruction, or service shutdown (`onDestroy`).
- **Null Safety**: Avoid non-null assertion operators (`!`) or forced unwrapping. Use Kotlin's null-safe operators (`?.`, `?:`) or explicit guard clauses.

## 2. Structural Architecture & Code Smells
- **Function Length & Single Responsibility**: Keep functions under 50 lines. Decompose monolithic classes (ViewModels, Controller classes) into single-responsibility components (e.g. UseCases, Repositories).
- **Atomic Design Compliance**: Ensure UI composables adhere to Atomic Design Principles (Atoms: 20-50 lines, Molecules: 50-100 lines, Organisms: 100-200 lines, Templates/Screens: 50-150 lines).
- **No Duplicate Event Processing**: Verify that state flows, channels, or event streams are collected in exactly one place per feature to avoid double event processing or duplicate network dispatches.

## 3. Performance Optimization
- **UI Thread Safety**: Never invoke blocking calls (`runBlocking`, `Thread.sleep()`, synchronous file I/O) on the main Android UI thread or ExoPlayer playback loops.
- **Stream Throttling**: Throttle high-frequency continuous data streams (camera frames, sensor inputs, live audio buffers) using `sample()` or `debounce()` before network transmission.
