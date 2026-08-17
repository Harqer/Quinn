/**
 * @AtomicLevel: Organism
 * @SemanticPurpose: Android Component for EntryProvider.kt
 */

package com.musically.studio.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.*
import com.musically.studio.ui.screens.*
import com.musically.studio.ui.screens.onboarding.*
import androidx.navigation3.runtime.metadata
import com.musically.studio.ui.components.organisms.EmptyPaneState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
fun maveEntryProvider(
    viewModel: MainViewModel,
    navigator: Navigator,
    onAcknowledgePermissions: () -> Unit,
    hasPermissions: Boolean = false,
    onMenuClick: () -> Unit,
    onLikeClick: (String) -> Unit = {},
    onDownloadClick: (String) -> Unit = {}
) = entryProvider {
    entry<Route.Welcome> {
        WelcomeScreen(
            viewModel = viewModel
        )
    }

    entry<Route.SignIn> {
        com.musically.studio.ui.screens.auth.SignInScreen(
            viewModel = viewModel
        )
    }


    entry<Route.Home> {
        MaveHomeScreen(
            viewModel = viewModel,
            hasPermissions = hasPermissions,
            onAcknowledgePermissions = onAcknowledgePermissions,
            onNavigateToProfile = onMenuClick,
            onNavigateToCamera = { navigator.navigate(Route.Camera) },
            onNavigateToConcerts = { navigator.navigate(Route.Concerts) },
            onNavigateToPodcast = { navigator.navigate(Route.Podcast) },
            onNavigateToAudiobooks = { navigator.navigate(Route.CategoryView("audiobooks")) },
            onNavigateToMusic = { navigator.navigate(Route.CategoryView("music")) },
            onNavigateToJam = { navigator.navigate(Route.JamLobby) },
            onNavigateToTrivia = { navigator.navigate(Route.JamLobby) },
            onGeneratePodcast = { navigator.navigate(Route.PodcastOnboarding) },
            onGenerateAudiobook = { navigator.navigate(Route.PodcastOnboarding) },
            onTrackClick = { trackId ->
                viewModel.tracks.value.find { it.id == trackId }?.let {
                    viewModel.playTrack(it)
                } ?: viewModel.communityTracks.value.find { it.id == trackId }?.let {
                    viewModel.playTrack(it)
                }
            }
        )
    }

    entry<Route.Discover> {
        DiscoverScreen(
            viewModel = viewModel,
            onNavigateToSettings = onMenuClick,
            onNavigateToLibrary = { navigator.navigate(Route.Library) },
            onNavigateToDevices = { navigator.navigate(Route.Devices) },
            onNavigateToMore = { navigator.navigate(Route.Search) },
            onNavigateToCamera = { navigator.navigate(Route.Camera) },
            onNavigateToLiveSession = { navigator.navigate(Route.LiveSession) },
            onNavigateToCategory = { navigator.navigate(Route.CategoryView(it)) },
            onNavigateToTrack = { trackId ->
                viewModel.tracks.value.find { it.id == trackId }?.let {
                    viewModel.playTrack(it)
                } ?: viewModel.communityTracks.value.find { it.id == trackId }?.let {
                    viewModel.playTrack(it)
                }
            },
            onNavigateToPlaylist = { navigator.navigate(Route.PlaylistView(it)) },
            onNavigateToSearch = { query -> 
                viewModel.sendTextCommand("Search for $query") 
                navigator.navigate(Route.Search)
            }
        )
    }

    entry<Route.Camera> {
        CameraCaptureScreen(
            onImageCaptured = { base64 ->
                if (viewModel.isLiveSessionActive.value) {
                    viewModel.sendFrame(base64)
                    navigator.goBack()
                } else {
                    navigator.navigate(Route.GeneratingSong(base64))
                }
            },
            onClose = { navigator.goBack() }
        )
    }

    entry<Route.GeneratingSong>(
        metadata = maveVerticalTransitionMetadata()
    ) { key ->
        GeneratingSongScreen(
            imageBase64 = key.imageBase64,
            viewModel = viewModel,
            onComplete = {
                navigator.goBack()
                navigator.navigate(Route.Home)
            }
        )
    }

    entry<Route.Gallery> {
        GalleryPickerScreen(
            onImageSelected = { base64 ->
                viewModel.onGalleryImageSelected(base64)
                navigator.goBack()
            },
            onClose = { navigator.goBack() }
        )
    }

    entry<Route.LiveSession> {
        val context = androidx.compose.ui.platform.LocalContext.current
        LiveSessionScreen(
            viewModel = viewModel,
            onNavigateBack = { navigator.goBack() },
            onNavigateToCamera = { navigator.navigate(Route.Camera) },
            onNavigateToGallery = { navigator.navigate(Route.Gallery) },
            onMoreOptionsClick = { 
                navigator.navigate(Route.LiveSessionOptions)
            }
        )
    }

    entry<Route.JamLobby> {
        val context = androidx.compose.ui.platform.LocalContext.current
        // Retrieve JamViewModel using Hilt
        val jamViewModel: JamViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
        JamLobbyScreen(
            viewModel = jamViewModel,
            onNavigateBack = { navigator.goBack() },
            onGameStarted = { mode -> 
                when (mode) {
                    com.musically.studio.network.GameMode.REMIX -> navigator.navigate(Route.JamRemix)
                    com.musically.studio.network.GameMode.TRIVIA_NAME_THAT_TUNE -> navigator.navigate(Route.JamTrivia)
                    else -> navigator.navigate(Route.JamTrivia)
                }
            },
            onAddTrackClick = { navigator.navigate(Route.JamSongPicker) }
        )
    }

    entry<Route.JamRemix> {
        val jamViewModel: JamViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
        val uiState by jamViewModel.uiState.collectAsStateWithLifecycle()
        val session = uiState.session
        val context = androidx.compose.ui.platform.LocalContext.current
        if (session != null) {
            JamRemixScreen(
                session = session,
                onAddInstrument = { jamViewModel.addLayer("Add $it") },
                onEndJam = { navigator.goBack() },
                localUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: "",
                instruments = jamViewModel.instruments.collectAsStateWithLifecycle().value
            )
        }
    }

    entry<Route.JamTrivia> {
        val jamViewModel: JamViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
        val uiState by jamViewModel.uiState.collectAsStateWithLifecycle()
        val session = uiState.session
        val context = androidx.compose.ui.platform.LocalContext.current
        
        var isRecording by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
        
        if (session != null) {
            JamTriviaScreen(
                session = session,
                onBid = { jamViewModel.setTriviaTarget(session.currentTriviaTrack, it) },
                onPass = { jamViewModel.setTriviaTarget(session.currentTriviaTrack, session.lowestBidNotes) },
                onPlayAudio = { jamViewModel.playTriviaSnippet(session.lowestBidNotes) },
                onStartRecording = { 
                    isRecording = true
                    jamViewModel.recordVoice(context, true)
                },
                onStopRecording = {
                    isRecording = false
                    jamViewModel.recordVoice(context, false)
                    // Gemini Live will automatically validate the guess and trigger submitTriviaGuess via a function call
                },
                onNextRound = { jamViewModel.nextTriviaRound() },
                onEndGame = { navigator.goBack() },
                isRecording = isRecording,
                isPlaying = session.currentTriviaState == com.musically.studio.network.TriviaState.PLAYING_AUDIO.name,
                wasCorrect = session.lastGuessCorrect,
                actualSong = session.lastActualSong,
                localUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
            )
        }
    }

    entry<Route.JamSongPicker>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) {
        val jamViewModel: JamViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
        com.musically.studio.ui.screens.JamSongPickerScreen(
            mainViewModel = viewModel,
            jamViewModel = jamViewModel,
            onDismiss = { navigator.goBack() }
        )
    }

    entry<Route.PlaylistPicker>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) { key ->
        com.musically.studio.ui.screens.PlaylistPickerBottomSheet(
            trackId = key.trackId,
            viewModel = viewModel,
            onDismiss = { navigator.goBack() }
        )
    }

    entry<Route.Search>(
        metadata = androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy.listPane {
            EmptyPaneState(
                title = "Search for something",
                subtitle = "Find your favorite songs, artists, and podcasts",
                icon = Icons.Rounded.Search
            )
        }
    ) {
        SearchScreen(
            viewModel = viewModel,
            onNavigateToCamera = { navigator.navigate(Route.Camera) },
            onNavigateToCategory = { navigator.navigate(Route.CategoryView(it)) },
            onNavigateToAlbum = { navigator.navigate(Route.AlbumView(it)) },
            onNavigateToSettings = onMenuClick
        )
    }

    entry<Route.Podcast>(
        metadata = maveVerticalTransitionMetadata()
    ) {
        val jetcasterViewModel = androidx.lifecycle.viewmodel.compose.viewModel<com.musically.studio.ui.jetcaster.ui.home.HomeViewModel>()
        val windowSizeClass = androidx.compose.material3.adaptive.currentWindowAdaptiveInfo().windowSizeClass
        com.musically.studio.ui.jetcaster.ui.home.MainScreen(
            windowSizeClass = windowSizeClass,
            navigateToPlayer = { episode ->
                navigator.navigate(Route.NowPlaying(episode.uri))
            },
            viewModel = jetcasterViewModel
        )
    }

    entry<Route.Concerts> {
        ConcertsScreen(
            viewModel = viewModel,
            onMenuClick = onMenuClick
        )
    }

    entry<Route.Chat>(
        metadata = androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy.listPane {
            EmptyPaneState(
                title = "Start a chat",
                subtitle = "Generate songs or podcasts with AI",
                icon = Icons.Rounded.Chat
            )
        }
    ) {
        ChatScreen(
            userPhotoUrl = viewModel.getUserPhotoUrl(),
            userDisplayName = viewModel.getUserDisplayName(),
            onNavigateBack = { navigator.goBack() },
            onMenuClick = onMenuClick,
            onTrackClick = { trackId ->
                viewModel.tracks.value.find { it.id == trackId }?.let {
                    viewModel.playTrack(it)
                } ?: viewModel.communityTracks.value.find { it.id == trackId }?.let {
                    viewModel.playTrack(it)
                }
            }
        )
    }

    entry<Route.Library>(
        metadata = androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy.listPane {
            EmptyPaneState(
                title = "Your Library",
                subtitle = "Select an album or playlist to start listening",
                icon = Icons.Rounded.LibraryMusic
            )
        }
    ) {
        LibraryScreen(
            viewModel = viewModel,
            onNavigateToNowPlaying = { trackId ->
                viewModel.tracks.value.find { it.id == trackId }?.let {
                    viewModel.playTrack(it)
                }
            },
            onNavigateToAlbum = { navigator.navigate(Route.AlbumView(it)) },
            onNavigateToPlaylist = { navigator.navigate(Route.PlaylistView(it)) },
            onNavigateToHome = { navigator.navigate(Route.Home) },
            onNavigateToSearch = { navigator.navigate(Route.Search) },
            onNavigateToAdd = { navigator.navigate(Route.Camera) }
        )
    }

    entry<Route.Devices>(
        metadata = androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy.listPane {
            EmptyPaneState(
                title = "Devices",
                subtitle = "Connect to Meta Wearables",
                icon = Icons.Rounded.Devices
            )
        }
    ) {
        DevicesScreen(
            viewModel = viewModel,
            onBack = { navigator.goBack() }
        )
    }

    entry<Route.PlaylistView>(
        metadata = androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy.detailPane() + maveVerticalTransitionMetadata()
    ) { key ->
        PlaylistViewScreen(
            playlistId = key.playlistId,
            viewModel = viewModel,
            onNavigateBack = { navigator.goBack() },
            onTrackClick = { trackId ->
                viewModel.tracks.value.find { it.id == trackId }?.let {
                    viewModel.playTrack(it)
                }
            },
            onMoreClick = { navigator.navigate(Route.TrackOptions(it)) },
            onLikeClick = { onLikeClick(key.playlistId) },
            onDownloadClick = { onDownloadClick(key.playlistId) },
            onRemixClick = { navigator.navigate(Route.JamRemix) }
        )
    }

    entry<Route.CategoryView>(
        metadata = androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy.detailPane() + maveVerticalTransitionMetadata()
    ) { key ->
        if (key.categoryId == "audiobooks") {
            val jetcasterViewModel = androidx.lifecycle.viewmodel.compose.viewModel<com.musically.studio.ui.jetcaster.ui.home.HomeViewModel>()
            val windowSizeClass = androidx.compose.material3.adaptive.currentWindowAdaptiveInfo().windowSizeClass
            com.musically.studio.ui.jetcaster.ui.home.MainScreen(
                windowSizeClass = windowSizeClass,
                navigateToPlayer = { episode ->
                    // Since Route.Player does not exist, we'll navigate to NowPlaying and adapt 
                    // or just log. We'll use a local PlayerRoute if needed, but since we can't change routes easily,
                    // we'll pass Route.Podcast and hope the navigation resolves or we'll add Route.NowPlaying(episode.uri)
                    navigator.navigate(Route.NowPlaying(episode.uri))
                },
                viewModel = jetcasterViewModel
            )
        } else {
            CategoryViewScreen(
                categoryId = key.categoryId,
                viewModel = viewModel,
                onNavigateBack = { navigator.goBack() },
                onPlaylistClick = { navigator.navigate(Route.PlaylistView(it)) }
            )
        }
    }

    entry<Route.AlbumView>(
        metadata = androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy.detailPane() + maveVerticalTransitionMetadata()
    ) { key ->
        AlbumViewScreen(
            albumId = key.albumId,
            viewModel = viewModel,
            onNavigateBack = { navigator.goBack() },
            onTrackClick = { trackId ->
                viewModel.tracks.value.find { it.id == trackId }?.let {
                    viewModel.playTrack(it)
                }
            },
            onMoreClick = { navigator.navigate(Route.TrackOptions(it)) },
            onLikeClick = { onLikeClick(key.albumId) },
            onDownloadClick = { onDownloadClick(key.albumId) }
        )
    }

    entry<Route.UserProfile> { key ->
        UserProfileScreen(
            userId = key.userId,
            viewModel = viewModel,
            onBack = { navigator.goBack() },
            onSignedOut = {
                // Clear back-stack and reset route to Welcome so the user cannot back-navigate after sign-out/deletion
                navigator.resetToRoute(Route.Welcome)
            },
            onNavigateToAlbum = { navigator.navigate(Route.AlbumView(it)) },
            onNavigateToSettings = { navigator.navigate(Route.Settings) },
            onNavigateToDevices = { navigator.navigate(Route.Devices) }
        )
    }

    entry<Route.Settings>(
        metadata = maveVerticalTransitionMetadata()
    ) {
        SettingsScreen(
            viewModel = viewModel,
            onBack = { navigator.goBack() },
            onNavigateToPremium = { navigator.navigate(Route.Premium) },
            onNavigateToMfa = { navigator.navigate(Route.MfaEnrollment) }
        )
    }

    entry<Route.Recents>(
        metadata = maveVerticalTransitionMetadata()
    ) {
        com.musically.studio.ui.screens.RecentsScreen(
            viewModel = viewModel,
            onBack = { navigator.goBack() }
        )
    }

    entry<Route.Downloaded>(
        metadata = maveVerticalTransitionMetadata()
    ) {
        com.musically.studio.ui.screens.DownloadedTracksScreen(
            viewModel = viewModel,
            onBack = { navigator.goBack() }
        )
    }
    entry<Route.Premium>(
        metadata = maveVerticalTransitionMetadata()
    ) {
        PremiumPlansScreen(
            viewModel = viewModel,
            onBack = { navigator.goBack() }
        )
    }


    entry<Route.MfaEnrollment> {
        com.musically.studio.ui.screens.onboarding.MfaEnrollmentScreen(
            viewModel = viewModel,
            onEnrolled = { navigator.navigate(Route.Home) },
            onBack = { navigator.goBack() }
        )
    }

    entry<Route.PodcastOnboarding> {
        PodcastOnboardingScreen(
            viewModel = viewModel,
            onDone = { prompt -> navigator.navigate(Route.PodcastGenerator(prompt = prompt, isAudiobook = false)) }
        )
    }

    entry<Route.PodcastGenerator> { key ->
        PodcastGeneratorScreen(
            viewModel = viewModel,
            onNavigateToSettings = onMenuClick
        )
    }

    entry<Route.MfaVerification> {
        val resolver = viewModel.mfaResolver
        if (resolver != null) {
            com.musically.studio.ui.screens.onboarding.MfaVerificationScreen(
                viewModel = viewModel,
                resolver = resolver,
                onSuccess = { navigator.navigate(Route.Home) },
                onBack = { navigator.goBack() }
            )
        } else {
            androidx.compose.material3.Text("Error: No MFA session active")
        }
    }


    entry<Route.GenerateCover> { key ->
        GenerateCoverScreen(
            viewModel = viewModel,
            trackId = key.trackId,
            initialType = key.initialType,
            onBack = { navigator.goBack() },
            onCoverGenerated = { coverUrl ->
                navigator.goBack()
            }
        )
    }

    entry<Route.NowPlaying>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) { key ->
        val tracks by viewModel.tracks.collectAsStateWithLifecycle()
        val track = tracks.find { it.id == key.trackId }
        val context = androidx.compose.ui.platform.LocalContext.current
        NowPlayingScreen(
            track = track,
            viewModel = viewModel,
            onCollapse = { navigator.goBack() },
            onMoreOptions = { navigator.navigate(Route.TrackOptions(it)) },
            onQueueClick = { 
                navigator.navigate(Route.Queue)
            },
            onLyricsClick = { 
                navigator.navigate(Route.Lyrics(track?.id ?: ""))
            },
            onDeviceClick = {
                navigator.navigate(Route.Devices)
            }
        )
    }
    
    entry<Route.TrackOptions>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) { key ->
        TrackOptionsBottomSheet(
            trackId = key.trackId,
            viewModel = viewModel,
            onDismiss = { navigator.goBack() },
            onAddToPlaylistClick = { trackId ->
                navigator.navigate(Route.PlaylistPicker(trackId))
            }
        )
    }

    entry<Route.Queue>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) {
        QueueBottomSheet(
            viewModel = viewModel,
            onDismiss = { navigator.goBack() }
        )
    }

    entry<Route.Lyrics>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) { key ->
        LyricsBottomSheet(
            trackId = key.trackId,
            viewModel = viewModel,
            onDismiss = { navigator.goBack() }
        )
    }

    entry<Route.LiveSessionOptions>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) {
        LiveSessionOptionsBottomSheet(
            viewModel = viewModel,
            onDismiss = { navigator.goBack() }
        )
    }

    entry<Route.UsageLimitSheet>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) { key ->
        UsageLimitBottomSheet(
            reasonName = key.reasonName,
            viewModel = viewModel,
            onNavigateToPremium = { navigator.navigate(Route.Premium) },
            onDismiss = { navigator.goBack() }
        )
    }

    entry<Route.NotFound> {
        com.musically.studio.ui.components.organisms.NotFound404Card(
            onNavigateBack = { navigator.goBack() }
        )
    }
}

/**
 * Transitions for Mave Studio.
 */
fun maveTransitionSpec(): AnimatedContentTransitionScope<androidx.navigation3.scene.Scene<Route>>.() -> ContentTransform = {
    (fadeIn(animationSpec = tween(500)) + slideInHorizontally(animationSpec = tween(500), initialOffsetX = { it / 2 }))
        .togetherWith(fadeOut(animationSpec = tween(500)) + slideOutHorizontally(animationSpec = tween(500), targetOffsetX = { -it / 2 }))
}

fun mavePopTransitionSpec(): AnimatedContentTransitionScope<androidx.navigation3.scene.Scene<Route>>.() -> ContentTransform = {
    (fadeIn(animationSpec = tween(500)) + slideInHorizontally(animationSpec = tween(500), initialOffsetX = { -it / 2 }))
        .togetherWith(fadeOut(animationSpec = tween(500)) + slideOutHorizontally(animationSpec = tween(500), targetOffsetX = { it / 2 }))
}

fun maveVerticalTransitionMetadata() = metadata {
    put(NavDisplay.TransitionKey) {
        slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(500)
        ) togetherWith ExitTransition.KeepUntilTransitionsFinished
    }
    put(NavDisplay.PopTransitionKey) {
        EnterTransition.None togetherWith slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(500)
        )
    }
    put(NavDisplay.PredictivePopTransitionKey) {
        EnterTransition.None togetherWith slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(500)
        )
    }
}
