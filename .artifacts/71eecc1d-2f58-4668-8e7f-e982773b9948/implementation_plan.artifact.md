# Musically: Conversational Podcast Generation & Spotify Integration

This plan refactors the **Musically Podcast** experience into a conversational interface where users can steer Quinn's narration via text or voice. It also integrates seamless "Save to Spotify" and "Share" functionality, aligning with the existing music creation flow.

## User Review Required

> [!IMPORTANT]
> **Conversational AI**: The Podcast screen is being transformed from a passive "listen" mode to an active "chat" mode. Users can provide feedback (e.g., "make it more mysterious") which Quinn will immediately incorporate into the narrative.

> [!NOTE]
> **Spotify Podcast Library**: Generated podcasts will be saved to a dedicated "Musically Podcasts" playlist in the user's Spotify account, ensuring they appear in the correct library section as requested.

## Proposed Changes

### 1. Unified Conversational Logic (Quinn Graph v2.1)

#### [MODIFY] [quinn-graph.ts](file:///home/shaolin/lyria/src/services/quinn-graph.ts)
- Enhance `podcastNarratorNode` to consume `userFeedback` state.
- Ensure the narrative evolution is stateful, remembering previous instructions during the session.

---

### 2. Android: Conversational Podcast UI

#### [MODIFY] [PodcastScreen.kt](file:///home/shaolin/lyria/app/src/main/java/com/musically/studio/ui/screens/PodcastScreen.kt)
- **Scaffold Integration**: Use `Scaffold` to place the chat input at the bottom while keeping the top app bar for navigation.
- **Chat History**: Implement a `LazyColumn` to display Quinn's generated narratives and the user's instructions.
- **Actions**: Add "Save to Spotify" (using `spotifyService`) and "Share" (using system Intent) to each narrative card.
- **Material 3**: Use `TextField` with rounded corners and expressive icons (`Mic`, `Send`).

#### [MODIFY] [MainViewModel.kt](file:///home/shaolin/lyria/app/src/main/java/com/musically/studio/ui/MainViewModel.kt)
- Add `podcastMessages` state to track the conversation.
- Add `sendPodcastCommand(text: String)` and `savePodcastToSpotify(trackId: String)`.

---

### 3. Web: Production Podcast Console

#### [MODIFY] [PodcastView.tsx](file:///home/shaolin/lyria/src/web/features/podcast/PodcastView.tsx)
- **Layout**: Position the chat input at the bottom of the viewport using Tailwind `fixed` or `sticky` positioning within a responsive container.
- **Transcript History**: Transform the single transcript view into a scrollable history of "segments."
- **Spotify Integration**: Implement the "Save to Spotify" button using a real API call to the backend.

---

### 4. Backend: Spotify Library Integration

#### [NEW] [src/routes/spotify.ts](file:///home/shaolin/lyria/src/routes/spotify.ts) (Add Endpoint)
- `POST /api/spotify/podcast/save`: Handles saving a generated segment as a track in the user's "Musically Podcasts" playlist.

#### [MODIFY] [MusicService.ts](file:///home/shaolin/lyria/src/services/MusicService.ts)
- Add helper method to handle "Podcast" specific saving logic, ensuring metadata (vibe, visual context) is preserved.

## Verification Plan

### Functional Testing
- **Chat Steering**: Send "Quinn, make it sound like a noir film" -> Verify Quinn's next narration uses noir-style language and tone.
- **Spotify Save**: Click "Save to Spotify" on a podcast segment -> Verify it appears in the "Musically Podcasts" playlist in the Spotify app.

### Quality Audit
- [x] **Zero-Stub Policy**: No placeholder "Coming Soon" or "Empty" states; real data flows through the entire stack.
- [x] **Material 3 Design**: Verify icons and components follow the latest Jetpack Compose and React standards.

***

**Do you approve of this conversational and Spotify-integrated Podcast evolution?**
