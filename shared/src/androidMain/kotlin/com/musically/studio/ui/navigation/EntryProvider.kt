package com.musically.studio.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
    onMenuClick: () -> Unit,
    onLikeClick: (String) -> Unit = {},
    onDownloadClick: (String) -> Unit = {}
) = entryProvider {
    entry<Route.Welcome> {
        WelcomeScreen(
            viewModel = viewModel,
            onSignUpClick = { navigator.navigate(Route.AuthOptions) },
            onLoginClick = { navigator.navigate(Route.Login) }
        )
    }

    entry<Route.AuthOptions> {
        val activity = androidx.activity.compose.LocalActivity.current
        AuthOptionsScreen(
            onEmailClick = { navigator.navigate(Route.EmailInput) },
            onGoogleClick = { viewModel.triggerGoogleSignIn() },
            onAppleClick = { viewModel.triggerAppleSignIn() },
            onLoginClick = { navigator.navigate(Route.Login) },
            onBackClick = { navigator.goBack() }
        )
    }

    entry<Route.Login> {
        LoginScreen(
            onLoginSuccess = { navigator.navigate(Route.Home) },
            onNavigateToSignUp = { navigator.navigate(Route.AuthOptions) },
            viewModel = viewModel
        )
    }

    entry<Route.Home> {
        MaveHomeScreen(
            viewModel = viewModel,
            onNavigateToProfile = onMenuClick,
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
            onNavigateToPlaylist = { navigator.navigate(Route.PlaylistView(it)) }
        )
    }

    entry<Route.Camera> {
        CameraCaptureScreen(
            onImageCaptured = { base64 ->
                if (viewModel.isLiveSessionActive.value) {
                    viewModel.sendFrame(base64)
                } else {
                    viewModel.generateMusicPrompts(base64)
                }
                navigator.goBack()
            },
            onClose = { navigator.goBack() }
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

    entry<Route.Search>(
        metadata = androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy.listPane {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) { 
                androidx.compose.material3.Text("Search for something", color = androidx.compose.ui.graphics.Color.Gray) 
            }
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
        PodcastGeneratorScreen(
            viewModel = viewModel,
            onNavigateToSettings = onMenuClick
        )
    }

    entry<Route.Chat>(
        metadata = androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy.listPane {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) { 
                androidx.compose.material3.Text("Chat", color = androidx.compose.ui.graphics.Color.Gray) 
            }
        }
    ) {
        ChatScreen(
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
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) { 
                androidx.compose.material3.Text("Select an item", color = androidx.compose.ui.graphics.Color.Gray) 
            }
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
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) { 
                androidx.compose.material3.Text("Devices", color = androidx.compose.ui.graphics.Color.Gray) 
            }
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
            onDownloadClick = { onDownloadClick(key.playlistId) }
        )
    }

    entry<Route.CategoryView>(
        metadata = androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy.detailPane() + maveVerticalTransitionMetadata()
    ) { key ->
        CategoryViewScreen(
            categoryId = key.categoryId,
            viewModel = viewModel,
            onNavigateBack = { navigator.goBack() },
            onPlaylistClick = { navigator.navigate(Route.PlaylistView(it)) }
        )
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
                // Clear back-stack to Login so the user cannot back-navigate after sign-out/deletion
                navigator.navigate(Route.Login)
            },
            onNavigateToAlbum = { navigator.navigate(Route.AlbumView(it)) },
            onNavigateToSettings = { navigator.navigate(Route.Settings) }
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

    entry<Route.Premium>(
        metadata = maveVerticalTransitionMetadata()
    ) {
        PremiumPlansScreen(
            viewModel = viewModel,
            onBack = { navigator.goBack() }
        )
    }

    entry<Route.EmailInput> {
        EmailLinkInputScreen(
            viewModel = viewModel,
            onBackClick = { navigator.goBack() }
        )
    }

    entry<Route.PasswordInput> {
        PasswordInputScreen(
            viewModel = viewModel,
            onNextClick = { navigator.navigate(Route.BirthdayInput) },
            onBackClick = { navigator.goBack() }
        )
    }

    entry<Route.BirthdayInput> {
        BirthdayInputScreen(
            viewModel = viewModel,
            onNextClick = { navigator.navigate(Route.GenderInput) },
            onBackClick = { navigator.goBack() }
        )
    }

    entry<Route.GenderInput> {
        GenderInputScreen(
            viewModel = viewModel,
            onNextClick = { navigator.navigate(Route.NameTerms) },
            onBackClick = { navigator.goBack() }
        )
    }

    entry<Route.NameTerms> {
        NameTermsScreen(
            viewModel = viewModel,
            onNextClick = { navigator.navigate(Route.Loading) },
            onBackClick = { navigator.goBack() }
        )
    }

    entry<Route.Loading> {
        LoadingScreen()
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2000)
            navigator.navigate(Route.Notification)
        }
    }

    entry<Route.Notification> {
        NotificationScreen(
            onTurnOn = { 
                onAcknowledgePermissions()
                navigator.navigate(Route.ArtistSelection)
            },
            onNotNow = { navigator.navigate(Route.ArtistSelection) }
        )
    }

    entry<Route.ArtistSelection> {
        ArtistSelectionScreen(
            viewModel = viewModel,
            onDone = { navigator.navigate(Route.Home) }
        )
    }

    entry<Route.MfaEnrollment> {
        com.musically.studio.ui.screens.onboarding.MfaEnrollmentScreen(
            viewModel = viewModel,
            onEnrolled = { navigator.navigate(Route.Home) },
            onBack = { navigator.goBack() }
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
            onDismiss = { navigator.goBack() }
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
