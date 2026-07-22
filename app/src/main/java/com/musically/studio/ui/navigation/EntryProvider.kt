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
import com.musically.studio.ui.screens.*
import com.musically.studio.ui.screens.onboarding.*

@OptIn(ExperimentalMaterial3Api::class)
fun maveEntryProvider(
    viewModel: MainViewModel,
    navigator: Navigator,
    onAcknowledgePermissions: () -> Unit
) = entryProvider {
    entry<Route.Welcome> {
        WelcomeScreen(
            viewModel = viewModel,
            onSignUpClick = { navigator.navigate(Route.AuthOptions) },
            onLoginClick = { navigator.navigate(Route.Login) }
        )
    }

    entry<Route.AuthOptions> {
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
            onNavigateToSignUp = { navigator.navigate(Route.Welcome) },
            viewModel = viewModel
        )
    }

    entry<Route.Home> {
        HomeScreen(
            viewModel = viewModel,
            onNavigateToSettings = { navigator.navigate(Route.UserProfile(viewModel.getUserId())) },
            onNavigateToLibrary = { navigator.navigate(Route.Library) },
            onNavigateToDevices = { navigator.navigate(Route.Devices) },
            onNavigateToMore = { navigator.navigate(Route.Library) },
            onNavigateToCamera = { navigator.navigate(Route.Camera) },
            onNavigateToLiveSession = { navigator.navigate(Route.LiveSession) }
        )
    }

    entry<Route.Camera> {
        CameraCaptureScreen(
            onImageCaptured = { base64 ->
                viewModel.generateMusicPrompts(base64)
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
        LiveSessionScreen(
            viewModel = viewModel,
            onNavigateBack = { navigator.goBack() },
            onNavigateToCamera = { navigator.navigate(Route.Camera) },
            onNavigateToGallery = { navigator.navigate(Route.Gallery) }
        )
    }

    entry<Route.Library> {
        LibraryScreen(
            viewModel = viewModel,
            onNavigateToNowPlaying = { trackId ->
                viewModel.tracks.value.find { it.id == trackId }?.let {
                    viewModel.playTrack(it)
                }
            },
            onNavigateToAlbum = { navigator.navigate(Route.AlbumView(it)) },
            onNavigateToHome = { navigator.navigate(Route.Home) }
        )
    }

    entry<Route.Devices> {
        DevicesScreen(
            viewModel = viewModel,
            onNavigateBack = { navigator.goBack() }
        )
    }

    entry<Route.AlbumView> { key ->
        AlbumViewScreen(
            albumId = key.albumId,
            viewModel = viewModel,
            onNavigateBack = { navigator.goBack() },
            onTrackClick = { trackId ->
                viewModel.tracks.value.find { it.id == trackId }?.let {
                    viewModel.playTrack(it)
                }
            }
        )
    }

    entry<Route.UserProfile> { key ->
        UserProfileScreen(
            userId = key.userId,
            viewModel = viewModel,
            onBack = { navigator.goBack() }
        )
    }

    entry<Route.EmailInput> {
        EmailInputScreen(
            viewModel = viewModel,
            onNextClick = { navigator.navigate(Route.PasswordInput) },
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

    entry<Route.NowPlaying>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) { key ->
        val tracks by viewModel.tracks.collectAsStateWithLifecycle()
        val track = tracks.find { it.id == key.trackId }
        NowPlayingScreen(
            track = track,
            viewModel = viewModel,
            onCollapse = { navigator.goBack() }
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
