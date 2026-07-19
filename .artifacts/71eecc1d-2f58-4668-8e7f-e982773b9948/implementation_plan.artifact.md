# UI/UX Alignment: Spotify Aesthetic & Functional Hardening

This plan addresses the unacceptable UI discrepancies in the Android app, ensuring it strictly follows the Spotify design language, color palette, and includes all requested "Like" and "Bookmark" functionality.

## User Review Required

> [!IMPORTANT]
> **Theme Overhaul**: I am replacing the default Material 3 colors with the Spotify palette: Pure Black (`#121212`), Spotify Green (`#1DB954`), and Deep Gray (`#212121`). This will apply globally to all screens.

> [!WARNING]
> **Studio Hub Redesign**: I am removing the "large camera box" and "dumb text" from the `HomeScreen`. The POV will be integrated as a background immersive layer or a subtle circular preview in the chat bar, keeping the focus on the conversational creation.

## Proposed Changes

### 1. Visual Foundation (Theme & Colors)

#### [MODIFY] [Color.kt](file:///home/shaolin/lyria/app/src/main/java/com/musically/studio/ui/theme/Color.kt)
- Define Spotify-accurate colors:
  - `SpotifyGreen`: `#1DB954`
  - `SpotifyBlack`: `#121212`
  - `SpotifyDarkGray`: `#212121`
  - `SpotifyLightGray`: `#535353`

#### [MODIFY] [Theme.kt](file:///home/shaolin/lyria/app/src/main/java/com/musically/studio/ui/theme/Theme.kt)
- Update `DarkColorScheme` to use `SpotifyBlack` as background and `SpotifyGreen` as primary.
- Disable dynamic colors by default to ensure the Spotify aesthetic is preserved on all devices.

---

### 2. Conversational Hub (Studio/Home)

#### [MODIFY] [HomeScreen.kt](file:///home/shaolin/lyria/app/src/main/java/com/musically/studio/ui/screens/HomeScreen.kt)
- **Remove** the 300dp camera box and "Enable POV" placeholders.
- **Background POV**: Implement the camera preview as a subtle, darkened background layer or a floating "Now Playing" style circular preview.
- **Spotify Chat Bar**: Refactor the input bar to match Spotify's "Search" bar style but with creation actions (Mic, Camera toggle).

#### [MODIFY] [TrackItems.kt](file:///home/shaolin/lyria/app/src/main/java/com/musically/studio/ui/components/TrackItems.kt)
- **Chat Hub Actions**: Add two distinct icon buttons to Quinn's bubbles:
  - **Heart (Like)**: `Icons.Default.FavoriteBorder` -> Wired to Spotify Like.
  - **Bookmark (Save)**: `Icons.Default.BookmarkBorder` -> Wired to Musically Collection.

---

### 3. Personal Collection (Library)

#### [MODIFY] [LibraryScreen.kt](file:///home/shaolin/lyria/app/src/main/java/com/musically/studio/ui/screens/LibraryScreen.kt)
- Redesign to mirror Spotify's "Your Library" layout (Grid/List of rounded rectangles).
- Add "Liked Vibes" and "My Podcasts" default categories.

---

### 4. Logic & Metadata

#### [MODIFY] [ChatMessage.kt](file:///home/shaolin/lyria/app/src/main/java/com/musically/studio/ui/models/ChatMessage.kt)
- Ensure the model carries `isLiked` and `isBookmarked` states to update UI instantly.

## Verification Plan

### UI Audit
- **Visual Comparison**: Run the app and compare side-by-side with Spotify.
- **Theme Check**: Verify that `#121212` and `#1DB954` are the dominant colors.

### Functional Verification
- [ ] **Like Vibe**: Click Heart -> Verify icon fills and API is called.
- [ ] **Bookmark Vibe**: Click Bookmark -> Verify item appears in Library.
- [ ] **Mode Sync**: Toggle Music/Podcast -> Verify chat bar hints and Quinn's responses update.

***

**Do you approve of this strict realignment with the Spotify aesthetic?**
