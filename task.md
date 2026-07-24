# Web Frontend Mock Cleanup Tasks

- [x] `src/web/features/library/LibraryScreen.tsx`: Import `<EmptyState>` and use it instead of the raw `<div>Your library is empty.</div>`.
- [x] `src/web/features/home/HomeScreen.tsx`: Retrieve the user profile dynamically (via `useAuth()`) and display the correct initial/avatar and a personalized greeting instead of the dummy "M" and "Welcome to Mave Studio".
- [x] `src/web/components/organisms/PlayerBar.tsx`: Render a polished disabled state or hide completely when no track is active, rather than showing raw `<div>Not playing</div>`.
- [x] `src/web/features/album/AlbumView.tsx`: Remove the hardcoded `dummyTracks` array and fetch the album tracklist via dynamic backend queries based on `useParams()`.
- [x] `src/web/features/dashboard/MainDashboard.tsx`: Ensure empty states inside the conversational sidebar utilize a structured `<EmptyState>` component rather than raw HTML.
- [x] `PodcastGeneratorScreen.tsx`: Replace the dummy `/placeholder-audio.mp3` with the dynamic audio blob returned by `NarrativeService`.
- [x] `AlbumView.tsx` & `LoginScreen.tsx`: Replace dummy background artwork and static routing params with real context states.

# Android UI Tasks
- [x] 1. Delete `WearableActivity.kt` and rewrite its UI functionality into `WearableStreamingService.kt` using `session.addDisplay().sendContent { flexBox {} }`.
- [x] 2. Update `ChatScreen.kt` to hydrate real track data from the backend instead of using the `dummy` ID, and fix placeholder images. Wire up Settings/Attachment buttons to simple snackbar/toast messages.
- [x] 3. Fix un-wired buttons in `NowPlayingScreen.kt`, `PlaylistViewScreen.kt`, `AlbumViewScreen.kt`, and `LiveSessionScreen.kt` by attaching `onClick` handlers that trigger state mutations or toasts.
- [x] 4. Refactor AppNavigation.kt to use Navigation 3 (NavDisplay).
- [x] 5. Use ListDetailSceneStrategy for adaptive layouts on LibraryScreen and SearchScreen.
- [x] 6. Create StudioAppFunctions.kt and expose "Play music", "Search podcast", and "Generate audiobook" commands.
- [x] 7. Create ComponentStyles.kt and migrate CustomButton and PlaybackBar to use Modifier.styleable.
- [x] 8. Enable enableEdgeToEdge() in MainActivity and handle insets uniformly.
