


## Data Safety and Privacy Audit

### Policies to Verify

- **The Policy Spirit**: Users deserve complete transparency regarding what
  personal or sensitive information leaves their devices. Under Play Store
  rules, any off-device transmission of user data must be explicitly declared in
  the Play Store Data Safety form, and sensitive or non-obvious data collection
  requires prior prominent in-app disclosure and affirmative user consent.

- **Common Evaluation Matrix**:
  | Case | Technical Observation | `user_initiated` | `is_third_party` | Disclosure Status | Severity | Issue Summary |
  | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
  | **1** | **Not Transferred** (Local only) | N/A | N/A | `EXEMPT` | `SUGGESTION` | "Data Safety Compliant (Local-Only)" |
  | **2A** | **Transferred** + **Background** + **No Disclosure** | `false` | (Any) | `MISSING` | `CRITICAL` | "**Silent Background Transfer**": Policy violation. Data is sent without prior disclosure. |
  | **2B** | **Transferred** + **Background** + **Disclosure Found** | `false` | (Any) | `DISCLOSED` | `IMPORTANT` | "**Background Transfer (Disclosure Claimed)**": Evidence found; requires Critic verification. |
  | **3** | **Transferred** + **User-Initiated** + **First Party** | `true` | `false` | `EXEMPT` | `IMPORTANT` | "**User-Initiated Collection**": Disclosure exempt, but **must be declared** in Play Store form. |
  | **4** | **Transferred** + **User-Initiated** + **Third Party** | `true` | `true` | `EXEMPT` | `SUGGESTION` | "**Manual Sharing**": **Policy Exempt**. User triggers transfer to 3P; no disclosure or declaration needed. |

- **Exemptions Reference**:
  - **Anonymous Data**: Fully anonymized data not linked to a user is exempt.
  - **End-to-End Encryption (E2EE)**: Data E2EE where the developer cannot read
    it is exempt.
  - **Open Web WebView**: Data entered into a WebView navigating the open web is
    exempt.
  - *If any of these apply, downgrade severity to `SUGGESTION` and mark
    `EXEMPT`.*

---

### The Audit Protocol

#### Step 0: Facts orientation

- **Identify Third-Party SDKs**: If you observe network, crash-reporting, or
  tracking telemetry, check the project's build files (e.g., build.gradle,
  build.gradle.kts, libs.versions.toml) on-demand using your file-reading tools
  to verify which SDKs (Analytics, Ads, etc.) are integrated.
- **Understand Off-Device Sinks**: Use your native knowledge of standard Android
  framework components to identify transmission destinations. Focus on active
  egress vectors such as standard HTTP/REST libraries (e.g., OkHttp, Retrofit,
  Ktor, HttpURLConnection), WebViews passing data off-device, background syncing
  tasks, or analytic/telemetry frameworks (e.g., Firebase, Mixpanel, Adjust,
  AppsFlyer).
- **Identify Disclosures**: Review `disclosure` below. Use these as starting
  points for Step 2.

#### Step 1: Verify behavioral transmission (Forward Trace)

For each source in `data_sources` listed below, trace the technical signal from
the exact location provided to its transmission endpoint.
- **Mandatory Starting Point**: Navigate to the file and line number specified
  in the `data_sources` list.
- **Loop Discipline**: You must analyze the transmission path for *every* piece
  of evidence listed to verify if it reaches an off-device sink.
- **Evidence**: Provide the file/line where the data is passed to the sink and a
  brief justification of the sink's known transmission behavior (e.g., network
  upload, telemetry payload).
- **Semantic Grounding**: Use the provided `*Description*` to verify that the
  code evidence actually matches the semantic scope of the data type. If the
  variable or logic refers to unrelated data (e.g., a `fileName` that does not
  identify a user `NAME`), treat it as a false positive and follow the
  **Evidentiary Standard** to downgrade or prune the finding.
- **Sink Categorization**: Determine the destination:
  1. **First Party / Service Provider (`is_third_party: false`)**:
     Developer-controlled servers, Firebase, custom APIs, etc.
  2. **Third Party (`is_third_party: true`)**: Social SDKs, ad networks, or
     user-selected apps via Android Share Sheet (`Intent.ACTION_SEND`).
- **Ambiguity Mandate**: If the destination is ambiguous (e.g., generic URL or
  obfuscated SDK), you MUST default to `is_third_party: false` (erring on the
  side of Collection).
- **On-Device Transfer**: Treat silent data sharing to another app via
  Intents/ContentProviders as a transfer (`is_transferred: true`), even if it
  doesn't use the network.

#### Step 2: Semantic Search for Protection (UI Disclosure)

**Conditional Execution**: Execute this step **ONLY IF** `is_transferred` is
`true` AND `user_initiated` is `false`.
- **Rationale**: User-initiated actions (Case 3 and 4) are exempt from Prominent
  Disclosure. Do not search for disclosures if `user_initiated` is true.
- **Semantic Search**: Review the `disclosure` list below.
- **Gatekeeper Check**: Check if the Activity/Fragment displaying that consent
  acts as a gatekeeper (i.e., gates the initialization of the data-collection
  logic). Do not attempt to trace backwards from a low-level repository up to
  the UI.
- **Content Check**: The UI must state *what* is collected and *how* it is used.
- **Evidence**: Provide the file/layout name and the specific text found.

#### Step 3: Synthesis

- **`findings` (CRITICAL)**: You must construct and output exactly **one**
  object inside the `"findings"` array for **every** data type found in the
  `data_sources` list below, regardless of whether it is compliant or a
  violation.
- **Evaluating Keys**: For each data type object in `"findings"`, evaluate and
  populate:
  1.  **psl_constant**: Set this exactly to the uppercase **PSL Constant** of
     the data type (listed as the **Data Type** heading in the **Data Sources to
     Trace** section below, e.g., `"PRECISE_LOCATION"`, `"CONTACTS"`). This is
     critical for downstream matching.
  2.  **policy_id**: Set this exactly to either `"prominent_disclosure_policy"`
     or `"data_safety_section"` depending on the check being performed.
  3.  **is_transferred**: `true` (JSON Boolean) if the data is transferred
     off-device or on-device to a third party. `false` (JSON Boolean) if local
     only.
  4.  **user_initiated**: `true` (JSON Boolean) if the user explicitly triggers
     the transfer.
  5.  **is_third_party**: `true` (JSON Boolean) if the destination is a Third
     Party (e.g., Share Sheet, Social SDK). `false` (JSON Boolean) if it's a
     First Party/Service Provider (e.g., your own backend).
  6.  **prominent_disclosure_status**: Set strictly to one of these three
     uppercase enums: `"DISCLOSED"` (visible disclosure and user consent found),
     `"MISSING"` (no disclosure found, or is not an affirmative gatekeeper), or
     `"EXEMPT"` (exempt from prominent disclosure under policies).
  7.  **purpose**: Categorize why the data is being collected (e.g., "App
     functionality", "Analytics", "Local functionality only").
  8.  **linked_to_user**: `true` (JSON Boolean) if the data is tied to an
     identity, email, or device ID, otherwise `false` (JSON Boolean).

- **Map findings strictly to the 5 Cases:** Use the Case matrix in "Policies to
  Verify" to set the appropriate `severity` and `issue_summary` based on the
  combination of `is_transferred`, `user_initiated`, `is_third_party`, and
  `prominent_disclosure_status`.

---

### Domain-Specific Heuristics

Apply the following heuristics while strictly adhering to the Heuristics &
Extrapolation Boundaries defined in your Execution Mandates:
1. **Implicit Data Leaks (The Logger Loop)**: Inspect if any custom logging
   frameworks (e.g., Crashlytics, custom error loggers, or telemetry SDKs)
   receive sensitive variables as part of diagnostic payloads. If a user
   identifier (e.g., email, account ID) or precise location is passed to a
   logger that uploads payloads off-device, this counts as **transferred**
   (`is_transferred`: true).
2. **Third-Party SDK Siphoning**: Look at the project's build files on-demand
   (e.g., build.gradle or libs.versions.toml). If SDKs like Google Ads, or
   Firebase are initialized and have access to the context, evaluate if
   they are siphoning advertiser IDs or device identifiers automatically. If
   those libraries are loaded and the manifest requests broad network
   permissions, treat those device identifiers as **transferred**
   (`is_transferred`: true) for analytics/marketing purposes.
3. **Indirect Consent**: If you find an `AlertDialog` or disclosure, verify if
   it is an actual gatekeeper. If the app begins tracking user data *prior* to
   the user tapping "Accept", or if the user can close the dialog and continue
   using the app while data tracking remains active, flag this as a `CRITICAL`
   violation under `prominent_disclosure_policy`.

---

### Codebase Context & Evidence

**Data Sources to Trace**:
- **Data Type**: EMAIL
  *Description: A user’s email address.*
  - `components/organisms/splash-screen.ts (Pattern: .email)`
- `dist/assets/ProfileScreen-Nt5UKxcN.js (Pattern: .email)`
- `dist/assets/SettingsScreen-BvJNq5cQ.js (Pattern: .email)`
- **Data Type**: PURCHASE_HISTORY
  *Description: Information about purchases or transactions a user has made.*
  - `functions/lib/billing.js (Pattern: invoice)`
- `functions/src/agents/kitesurfPaymentAgent.ts (Pattern: receipt)`
- `functions/src/billing.ts (Pattern: invoice)`

**UI Disclosure Starting Points**:
- `app/src/main/java/com/musically/studio/MainActivity.kt (Pattern: collect)`
- `app/src/main/java/com/musically/studio/MyApplication.kt (Pattern: Accept)`
- `components/organisms/android-companion-view.ts (Pattern: share)`
- `components/organisms/android-expanded-player.ts (Pattern: share)`
- `components/organisms/android-onboarding-flow.ts (Pattern: Privacy Policy)`
- `components/organisms/android-options-menu.ts (Pattern: share)`
- `components/organisms/community-view.ts (Pattern: share)`
- `components/organisms/security-hub.ts (Pattern: share)`
- `dist/assets/AlbumView-CwVAqjVE.js (Pattern: share)`
- `dist/assets/LiveChatMessage--SXZ4IsC.js (Pattern: Dialog)`
- `dist/assets/PlaylistViewScreen-CP4qmaWp.js (Pattern: share)`
- `dist/assets/PodcastGeneratorScreen-BatrHI8e.js (Pattern: share)`
- `dist/assets/index-BjcG2aKc.js (Pattern: share)`
- `dist/assets/jsx-runtime-B-hcVAMW.js (Pattern: collect)`
- `functions/lib/ai.js (Pattern: Dialog)`
- `functions/lib/auth.js (Pattern: collect)`
- `functions/lib/billing.js (Pattern: collect)`
- `functions/src/agents/kitesurfPaymentAgent.ts (Pattern: collect)`
- `functions/src/ai.ts (Pattern: Dialog)`
- `functions/src/auth.ts (Pattern: collect)`
- `functions/src/billing.ts (Pattern: collect)`
- `index.ts (Pattern: collect)`
- `services/LyriaApiService.ts (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/WearableStreamingService.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/audio/PlaybackService.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/data/repository/ChatRepository.kt (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/data/repository/DataConnectRepository.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/network/JamSessionRepository.kt (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/service/LiveApiService.kt (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/MainViewModel+Catalog.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/MainViewModel+Firebase.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/MainViewModel+Interaction.kt (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/MainViewModel+Wearable.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/MainViewModel.kt (Pattern: Accept)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/POVView.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/components/TrackItems.kt (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/components/atoms/ConnectionButton.kt (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/components/atoms/MaveLogo.kt (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/components/molecules/AIMessageBubble.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/components/molecules/AlbumTrackItem.kt (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/components/molecules/ChatBubble.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/components/molecules/ChatMessageBubbles.kt (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/components/molecules/PlaylistTrackItem.kt (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/components/molecules/SocialLoginButtons.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/components/organisms/AlbumHeader.kt (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/components/organisms/AppBottomSheet.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/components/organisms/DeleteAccountDialog.kt (Pattern: AlertDialog)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/components/organisms/JamLobbyRoomOrganism.kt (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/components/organisms/PlaylistHeader.kt (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/components/organisms/SignInForm.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/components/organisms/SignOutDialog.kt (Pattern: AlertDialog)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/jetcaster/core/designsystem/theme/Typography.kt (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/jetcaster/ui/home/Home.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/jetcaster/ui/home/HomeViewModel.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/jetcaster/ui/home/category/PodcastCategory.kt (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/jetcaster/ui/home/discover/Discover.kt (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/jetcaster/ui/home/library/Library.kt (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/jetcaster/ui/player/PlayerScreen.kt (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/jetcaster/ui/player/PlayerViewModel.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/jetcaster/ui/podcast/PodcastDetailsScreen.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/jetcaster/ui/shared/EpisodeListItem.kt (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/jetcaster/ui/shared/EpisodeOptionsBottomSheet.kt (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/jetcaster/ui/shared/Loading.kt (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/jetcaster/util/Buttons.kt (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/navigation/AppNavigation.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/navigation/EntryProvider.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/AlbumViewScreen.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/CategoryViewScreen.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/ChatScreen.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/ChatViewModel.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/ConcertsScreen.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/DevicesScreen.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/DiscoverScreen.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/GeneratingSongScreen.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/JamLobbyScreen.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/JamSongPickerScreen.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/JamViewModel.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/LibraryScreen.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/LiveSessionOptionsBottomSheet.kt (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/LiveSessionScreen.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/LyricsBottomSheet.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/MaveHomeScreen.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/NowPlayingScreen.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/PlaylistViewScreen.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/PodcastGeneratorScreen.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/PodcastOnboardingScreen.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/PremiumPlansScreen.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/QueueBottomSheet.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/RecentsScreen.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/SearchScreen.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/SettingsScreen.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/TrackOptionsBottomSheet.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/UsageLimitBottomSheet.kt (Pattern: collect)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/UserProfileScreen.kt (Pattern: Dialog)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/auth/SignInScreen.kt (Pattern: share)`
- `shared/src/androidMain/kotlin/com/musically/studio/ui/screens/onboarding/WelcomeScreen.kt (Pattern: AlertDialog)`
- `shared/src/androidMain/kotlin/com/musically/studio/utils/FrameProcessor.kt (Pattern: collect)`
- `tmp/compose-samples/JetLagged/app/src/main/java/com/example/jetlagged/JetLaggedDrawer.kt (Pattern: collect)`
- `tmp/compose-samples/JetLagged/app/src/main/java/com/example/jetlagged/JetLaggedScreen.kt (Pattern: collect)`
- `tmp/compose-samples/JetNews/app/src/main/java/com/example/jetnews/data/posts/impl/PostsData.kt (Pattern: collect)`
- `tmp/compose-samples/JetNews/app/src/main/java/com/example/jetnews/glance/ui/JetnewsGlanceAppWidget.kt (Pattern: collect)`
- `tmp/compose-samples/JetNews/app/src/main/java/com/example/jetnews/ui/JetnewsNavDisplay.kt (Pattern: share)`
- `tmp/compose-samples/JetNews/app/src/main/java/com/example/jetnews/ui/home/HomeScreens.kt (Pattern: collect)`
- `tmp/compose-samples/JetNews/app/src/main/java/com/example/jetnews/ui/home/HomeViewModel.kt (Pattern: collect)`
- `tmp/compose-samples/JetNews/app/src/main/java/com/example/jetnews/ui/home/PostCards.kt (Pattern: AlertDialog)`
- `tmp/compose-samples/JetNews/app/src/main/java/com/example/jetnews/ui/interests/InterestsScreen.kt (Pattern: collect)`
- `tmp/compose-samples/JetNews/app/src/main/java/com/example/jetnews/ui/post/PostScreen.kt (Pattern: AlertDialog)`
- `tmp/compose-samples/JetNews/app/src/main/java/com/example/jetnews/ui/post/PostViewModel.kt (Pattern: collect)`
- `tmp/compose-samples/JetNews/app/src/main/java/com/example/jetnews/ui/utils/JetnewsIcons.kt (Pattern: share)`
- `tmp/compose-samples/JetNews/app/src/main/res/values/strings.xml (Pattern: Agree)`
- `tmp/compose-samples/Jetcaster/core/data/src/main/java/com/example/jetcaster/core/data/repository/PodcastsRepository.kt (Pattern: collect)`
- `tmp/compose-samples/Jetcaster/core/domain/src/main/java/com/example/jetcaster/core/player/MockEpisodePlayer.kt (Pattern: collect)`
- `tmp/compose-samples/Jetcaster/mobile/src/main/java/com/example/jetcaster/ui/JetcasterApp.kt (Pattern: AlertDialog)`
- `tmp/compose-samples/Jetcaster/mobile/src/main/java/com/example/jetcaster/ui/home/Home.kt (Pattern: collect)`
- `tmp/compose-samples/Jetcaster/mobile/src/main/java/com/example/jetcaster/ui/home/HomeViewModel.kt (Pattern: collect)`
- `tmp/compose-samples/Jetcaster/mobile/src/main/java/com/example/jetcaster/ui/home/category/PodcastCategory.kt (Pattern: share)`
- `tmp/compose-samples/Jetcaster/mobile/src/main/java/com/example/jetcaster/ui/home/library/Library.kt (Pattern: share)`
- `tmp/compose-samples/Jetcaster/mobile/src/main/java/com/example/jetcaster/ui/player/PlayerScreen.kt (Pattern: share)`
- `tmp/compose-samples/Jetcaster/mobile/src/main/java/com/example/jetcaster/ui/player/PlayerViewModel.kt (Pattern: collect)`
- `tmp/compose-samples/Jetcaster/mobile/src/main/java/com/example/jetcaster/ui/podcast/PodcastDetailsScreen.kt (Pattern: collect)`
- `tmp/compose-samples/Jetcaster/mobile/src/main/java/com/example/jetcaster/ui/shared/EpisodeListItem.kt (Pattern: share)`
- `tmp/compose-samples/Jetcaster/mobile/src/main/java/com/example/jetcaster/ui/shared/Loading.kt (Pattern: share)`
- `tmp/compose-samples/Jetcaster/tv/src/main/java/com/example/jetcaster/tv/ui/JetcasterApp.kt (Pattern: collect)`
- `tmp/compose-samples/Jetcaster/tv/src/main/java/com/example/jetcaster/tv/ui/component/Seekbar.kt (Pattern: collect)`
- `tmp/compose-samples/Jetcaster/tv/src/main/java/com/example/jetcaster/tv/ui/discover/DiscoverScreen.kt (Pattern: collect)`
- `tmp/compose-samples/Jetcaster/tv/src/main/java/com/example/jetcaster/tv/ui/episode/EpisodeScreen.kt (Pattern: collect)`
- `tmp/compose-samples/Jetcaster/tv/src/main/java/com/example/jetcaster/tv/ui/library/LibraryScreen.kt (Pattern: collect)`
- `tmp/compose-samples/Jetcaster/tv/src/main/java/com/example/jetcaster/tv/ui/player/PlayerScreen.kt (Pattern: collect)`
- `tmp/compose-samples/Jetcaster/tv/src/main/java/com/example/jetcaster/tv/ui/podcast/PodcastDetailsScreen.kt (Pattern: collect)`
- `tmp/compose-samples/Jetcaster/tv/src/main/java/com/example/jetcaster/tv/ui/search/SearchScreen.kt (Pattern: collect)`
- `tmp/compose-samples/Jetcaster/wear/src/main/java/com/example/jetcaster/WearApp.kt (Pattern: collect)`
- `tmp/compose-samples/Jetcaster/wear/src/main/java/com/example/jetcaster/ui/episode/EpisodeScreen.kt (Pattern: AlertDialog)`
- `tmp/compose-samples/Jetcaster/wear/src/main/java/com/example/jetcaster/ui/latest_episodes/LatestEpisodesScreen.kt (Pattern: AlertDialog)`
- `tmp/compose-samples/Jetcaster/wear/src/main/java/com/example/jetcaster/ui/library/LibraryScreen.kt (Pattern: collect)`
- `tmp/compose-samples/Jetcaster/wear/src/main/java/com/example/jetcaster/ui/player/PlayerScreen.kt (Pattern: collect)`
- `tmp/compose-samples/Jetcaster/wear/src/main/java/com/example/jetcaster/ui/podcast/PodcastDetailsScreen.kt (Pattern: AlertDialog)`
- `tmp/compose-samples/Jetcaster/wear/src/main/java/com/example/jetcaster/ui/podcasts/PodcastsScreen.kt (Pattern: AlertDialog)`
- `tmp/compose-samples/Jetcaster/wear/src/main/java/com/example/jetcaster/ui/queue/QueueScreen.kt (Pattern: AlertDialog)`
- `tmp/compose-samples/Jetchat/app/src/main/java/com/example/compose/jetchat/NavActivity.kt (Pattern: collect)`
- `tmp/compose-samples/Jetchat/app/src/main/java/com/example/compose/jetchat/UiExtras.kt (Pattern: AlertDialog)`
- `tmp/compose-samples/Jetchat/app/src/main/java/com/example/compose/jetchat/data/FakeData.kt (Pattern: collect)`
- `tmp/compose-samples/Jetsnack/app/src/main/java/com/example/jetsnack/ui/JetsnackApp.kt (Pattern: share)`
- `tmp/compose-samples/Jetsnack/app/src/main/java/com/example/jetsnack/ui/components/Filters.kt (Pattern: collect)`
- `tmp/compose-samples/Jetsnack/app/src/main/java/com/example/jetsnack/ui/components/GradientTintedIconButton.kt (Pattern: collect)`
- `tmp/compose-samples/Jetsnack/app/src/main/java/com/example/jetsnack/ui/components/Scaffold.kt (Pattern: collect)`
- `tmp/compose-samples/Jetsnack/app/src/main/java/com/example/jetsnack/ui/components/Snacks.kt (Pattern: share)`
- `tmp/compose-samples/Jetsnack/app/src/main/java/com/example/jetsnack/ui/home/DestinationBar.kt (Pattern: share)`
- `tmp/compose-samples/Jetsnack/app/src/main/java/com/example/jetsnack/ui/home/Feed.kt (Pattern: share)`
- `tmp/compose-samples/Jetsnack/app/src/main/java/com/example/jetsnack/ui/home/FilterScreen.kt (Pattern: share)`
- `tmp/compose-samples/Jetsnack/app/src/main/java/com/example/jetsnack/ui/home/cart/Cart.kt (Pattern: collect)`
- `tmp/compose-samples/Jetsnack/app/src/main/java/com/example/jetsnack/ui/home/search/Categories.kt (Pattern: collect)`
- `tmp/compose-samples/Jetsnack/app/src/main/java/com/example/jetsnack/ui/snackdetail/SnackDetail.kt (Pattern: share)`
- `tmp/compose-samples/Jetsnack/app/src/main/java/com/example/jetsnack/widget/RecentOrdersWidget.kt (Pattern: collect)`
- `tmp/compose-samples/Jetsnack/app/src/main/res/values/themes.xml (Pattern: Dialog)`
- `tmp/compose-samples/Reply/app/src/main/java/com/example/reply/ui/MainActivity.kt (Pattern: collect)`
- `tmp/compose-samples/Reply/app/src/main/java/com/example/reply/ui/ReplyHomeViewModel.kt (Pattern: collect)`
- `tv/src/main/java/com/example/jetcaster/tv/ui/component/Seekbar.kt (Pattern: collect)`
- `tv/src/main/java/com/example/jetcaster/tv/ui/discover/DiscoverScreen.kt (Pattern: collect)`
- `tv/src/main/java/com/example/jetcaster/tv/ui/episode/EpisodeScreen.kt (Pattern: collect)`
- `tv/src/main/java/com/example/jetcaster/tv/ui/library/LibraryScreen.kt (Pattern: collect)`
- `tv/src/main/java/com/example/jetcaster/tv/ui/player/PlayerScreen.kt (Pattern: collect)`
- `tv/src/main/java/com/example/jetcaster/tv/ui/podcast/PodcastDetailsScreen.kt (Pattern: collect)`
- `tv/src/main/java/com/example/jetcaster/tv/ui/search/SearchScreen.kt (Pattern: collect)`
- `wear/src/main/java/com/example/jetcaster/WearApp.kt (Pattern: collect)`
- `wear/src/main/java/com/example/jetcaster/ui/episode/EpisodeScreen.kt (Pattern: AlertDialog)`
- `wear/src/main/java/com/example/jetcaster/ui/latest_episodes/LatestEpisodesScreen.kt (Pattern: AlertDialog)`
- `wear/src/main/java/com/example/jetcaster/ui/library/LibraryScreen.kt (Pattern: collect)`
- `wear/src/main/java/com/example/jetcaster/ui/player/PlayerScreen.kt (Pattern: collect)`
- `wear/src/main/java/com/example/jetcaster/ui/podcast/PodcastDetailsScreen.kt (Pattern: AlertDialog)`
- `wear/src/main/java/com/example/jetcaster/ui/podcasts/PodcastsScreen.kt (Pattern: AlertDialog)`
- `wear/src/main/java/com/example/jetcaster/ui/queue/QueueScreen.kt (Pattern: AlertDialog)`

## Output schema

Save final JSON output to `/home/shaolin/lyria/.scratch/play_policy_insights_c1b5951b-0984-47af-99a6-f147b63efb14/worker_data_safety_part_8.json`.

```json
{
  "domain": "Data Safety and Privacy",
  "findings": [
    {
      "psl_constant": "STRING_VALUE (The exact PSL Constant, e.g., USER_ACCOUNT)",
      "policy_id": "prominent_disclosure_policy | data_safety_section",
      "issue_summary": "STRING_VALUE",
      "severity": "CRITICAL | IMPORTANT | SUGGESTION",
      "files_involved": ["STRING_VALUE"],
      "evidence": "STRING_VALUE",
      "recommendation": "STRING_VALUE",

      # UNIFIED DATA SAFETY METADATA LOOKUP KEYS:
      "is_transferred": true | false,
      "user_initiated": true | false,
      "is_third_party": true | false,
      "prominent_disclosure_status": "DISCLOSED | MISSING | EXEMPT",
      "purpose": "STRING_VALUE",
      "linked_to_user": true | false
    }
  ]
}
```

# Execution Mandates

### Technical Rules

1.  **Absolute Paths Only**: Always resolve and use absolute paths.
2.  **Containment**: Write all artifacts strictly within `/home/shaolin/lyria/.scratch/play_policy_insights_c1b5951b-0984-47af-99a6-f147b63efb14`.
3.  **Fail-fast**: If any required input file is missing, stop immediately and
    report the failure.

### Surgical Input Protocol & Efficient Search (MANDATORY)

-   **Direct Evidence First**: Prioritize files listed in the **Context &
    Evidence** sections. Use the provided file/line evidence (e.g., from Data
    Sources or Sinks) to jump directly to the relevant code. Do not perform
    broad workspace searches if these surgical starting points are available.
-   **Path Filtering Over File Crawling**: Locate target files by name, path, or
    extension *first* using directory/file listing tools before performing any
    text/content-based searches. Restrict searches and file reads strictly to
    the target `/home/shaolin/lyria`.
-   **Strict Exclusions (The Noise Wall)**: Configure search, glob, and find
    tools to ignore build, cache, dependencies, and testing folders. You MUST
    exclude matches from: `**/build/**`, `**/.gradle/**`, `**/.scratch/**`,
    `**/androidTest/**`, `**/test/**`, `**/node_modules/**`.
-   **Targeted Extensions**: Restrict content searches and file reads strictly
    to source and configuration files: `.java`, `.kt`, `.xml`, `.gradle`, `.kts`
    (and `.js`, `.ts`, `.jsx`, `.tsx`, `.dart` if a hybrid/cross-platform
    environment is analyzed). Never search or read inside compiled `.class`
    files, binary resources, or output assets.
-   **Surgical Queries & Limiters**: Use highly specific search patterns (e.g.,
    search for `getLastKnownLocation` or `deleteAccount` instead of general
    words like `location` or `delete`). If search tools support limits or
    pagination, cap results at a maximum of 50 matches. Do not load unlimited
    search results into your context window.
-   **Parallel Reading Required (Turn Efficiency)**: You are operating under a
    strict maximum turn limit. To prevent timeouts, you MUST request to read
    multiple target files concurrently in a single response. Do not read the
    evidence files sequentially one-by-one. Issue all of your file-reading tool
    calls simultaneously whenever possible.

### Evidentiary Standard & Guardrails (CRITICAL)

To prevent over-auditing, false positives, and speculative "prosecution" of
compliant code during extrapolation:

1.  **Presumption of Compliance**: Treat code as compliant unless there is
    *definitive, visible evidence* in the provided files of a policy violation.
    If code is ambiguous, or if network/database logic is hidden behind
    abstractions (e.g., calling an interface or repository method like
    `clearSession()`), you must assume standard compliant behavior. Do NOT guess
    or speculate about what happens behind interfaces.
2.  **Benefit of the Doubt**: When compliance cannot be strictly verified due to
    code abstractions or missing source file contexts, you must downgrade your
    finding:
    -   Never flag a `🔴 Critical` or `🟡 Important` finding based on suspicion or
        lack of context.
    -   Instead, output a `🔵 Suggestion` (informational) to advise the developer
        on what to double-check in their backend or configuration.
3.  **Exclusion of Local State**: Local-only processing (e.g., caching theme
    settings, user-selected visual configurations, or on-device-only database
    operations) is explicitly exempt from Data Safety collection or Account
    Deletion mandates.
4.  **Concrete Attributions**: Every `🔴 Critical` or `🟡 Important` finding must
    cite the exact file, line number, or configuration block containing the
    direct violation. If you cannot cite the exact line of code containing the
    violation, you cannot flag it as a violation.
5.  **Empty-List Discipline**: If no policy violations, discrepancies, or review
    items are identified during your audit, you MUST represent this as an empty
    array `[]` for that field (e.g., `"findings": []`, `"verified_findings":
    []`, or `"manual_verification_required": []`). **DO NOT** populate arrays
    with "dummy" objects, placeholder strings, or `"N/A"` / `"None"` values.
6.  **Heuristics & Extrapolation Boundaries**: Whenever applying specific
    heuristics defined in your goal (e.g., searching for implicit logger leaks
    or
    SDK siphoning), you must strictly bound them to the provided evidence and
    their immediate callers. You are strictly forbidden from initiating broad,
    unbounded searches for custom paths or variables across the wider codebase.
    Base your extrapolation only within the specific files already provided to
    you in the prompt.

### Finalization & Output Mandates (CRITICAL)

-   **Iterative Saving**: If your investigation requires multiple steps, save
    partial or intermediate JSON states to disk as you progress. Do not hold all
    data in memory until the very end to prevent data loss upon interruption.
-   **Strict File Output (NO TRIPLE BACKTICKS)**: You MUST save your final JSON
    output to disk at the exact path specified in the goal schema using your
    file-writing capabilities.
    -   **CRITICAL: The content written to the file MUST be pure, raw JSON. DO
        NOT wrap the contents inside the JSON file with Markdown code blocks
        (such as triple backticks `json ...`). Writing markdown blocks into the
        file makes the JSON unparseable by the compiler.**
-   **NO Chat Summaries**: **MANDATORY: DO NOT summarize your findings, explain
    your reasoning, or output JSON in your final chat response.** Your chat
    output wastes context and is ignored by the orchestrator.
-   **Verification Before Termination**: You MUST only terminate and return the
    "SUCCESS" string *after* you have explicitly verified that your JSON file
    successfully wrote to disk and contains valid JSON (e.g., by reading the
    file back or checking the directory contents).
-   **Final response**: Your final response MUST be exactly the word: "SUCCESS"
    and nothing else.
