# Gemini Live Integration

**Rule**: All modules (Web, Android Mobile, TV, Wear) share the `GeminiLiveManager` logic via the common `MainViewModel` or orchestrator.

## Principles
1. **Connection State**: The connection state is represented as a boolean (`isConnected`), accessed via `viewModel.geminiLiveManager.connectionState.collectAsState(initial = false)`.
2. **Transcripts**: The real-time output text from Gemini is exposed via `viewModel.geminiLiveManager.transcripts.collectAsState(initial = "")`.
3. **Cross-Platform Compatibility**: Always use platform-specific components (e.g., `androidx.tv.material3.Button` for TV, `androidx.wear.compose.material3.Button` for Wear) to render the shared state.
