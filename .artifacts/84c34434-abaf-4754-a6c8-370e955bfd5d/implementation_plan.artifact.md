# Implementation Plan - Frontend Production Readiness Audit & Remediation

This plan outlines the steps to audit and remediate mock data, placeholders, and stubs in the Musically Studio Android and Web frontends to ensure they are production-ready.

## User Review Required

> [!IMPORTANT]
> The remediation involves replacing several simulated behaviors with real implementations (e.g., voice recording, Spotify integration). This may require additional API keys or hardware permissions.

- **Spotify Integration**: We need to decide whether to use the Spotify App Remote (requires Spotify app installed) or the Web API for playback control.
- **Voice Recording**: Real-time voice streaming to the Quinn AI backend will increase bandwidth usage.

## Proposed Changes

### Android Project remediation

#### [MainViewModel.kt](file:///home/shaolin/lyria/app/src/main/java/com/musically/studio/ui/MainViewModel.kt)
- **Implement `recordVoice()`**: Integrate `AudioRecord` to capture live audio and stream it via `QuinnSessionManager`.
- **Implement Playback Logic**: Use `ExoPlayer` to handle track streaming. Update `isPlaying`, `trackProgress`, and `currentPlayingTrack` accordingly.
- **Fix Compilation Errors**: Define `setWearableConnected(Boolean)` and `setSpotifyConnected(Boolean)` methods used by UI screens.
- **Spotify Authentication**: Replace the hardcoded URL in `connectSpotify()` with a proper OAuth2 flow using the Spotify SDK.

#### [NowPlayingScreen.kt](file:///home/shaolin/lyria/app/src/main/java/com/musically/studio/ui/screens/NowPlayingScreen.kt)
- **Dynamic Timestamps**: Replace "0:00" and "3:00" placeholders with live duration and position data from the `MainViewModel`.

#### [OnboardingScreen.kt](file:///home/shaolin/lyria/app/src/main/java/com/musically/studio/ui/screens/OnboardingScreen.kt)
- **Asset Integration**: Replace the placeholder `MusicNote` icon with the official Musically Studio logo asset.
- **Localization**: Externalize all hardcoded strings to `strings.xml`.

---

### Web Project remediation

#### [HomeScreen.tsx](file:///home/shaolin/lyria/src/web/features/home/HomeScreen.tsx)
- **Dynamic Recently Played**: Replace hardcoded cards ("The Beatles", "Lana Del Rey") with actual data from `userTracks`.
- **Wrapped Section**: Update the "2021 in review" placeholder to dynamically show the current year or hide it if no data is available.

#### [AlbumView.tsx](file:///home/shaolin/lyria/src/web/features/album/AlbumView.tsx)
- **Data Fetching**: Remove the hardcoded Beatles content. Implement an `albumId` prop and fetch the album details (tracks, art, description) from the `/api/music/album/:id` endpoint.

#### [LibraryScreen.tsx](file:///home/shaolin/lyria/src/web/features/library/LibraryScreen.tsx)
- **Live Library Items**: Replace the hardcoded list with real playlists and artists fetched from the `/api/music/user/library` endpoint.

## Verification Plan

### Automated Tests
- **Unit Tests**: Run existing Vitest and JUnit tests to ensure no regressions.
- **Integration Tests**: Verify that `useTracks` and `MainViewModel` correctly handle empty or error states from the backend APIs.

### Manual Verification
- **Android**: Deploy the app to a physical device and test the "Allow Access & Continue" flow in Onboarding.
- **Web**: Log in via Firebase and verify that the "Recently played" section on the Home screen reflects actual account history.
- **Voice Test**: Trigger `recordVoice()` and verify that audio bytes are successfully sent over the WebSocket.
