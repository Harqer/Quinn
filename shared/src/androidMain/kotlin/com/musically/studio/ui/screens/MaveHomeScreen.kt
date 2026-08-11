package com.musically.studio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.*
import com.musically.studio.ui.components.organisms.*
import com.musically.studio.ui.components.molecules.MediaCard
import com.musically.studio.ui.components.organisms.MediaCarousel
import com.musically.studio.dataconnect.instance
import com.musically.studio.dataconnect.execute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaveHomeScreen(
    viewModel: MainViewModel,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToConcerts: () -> Unit = {},
    onNavigateToPodcast: () -> Unit = {},
    onNavigateToAudiobooks: () -> Unit = {},
    onNavigateToMusic: () -> Unit = {},
    onNavigateToCamera: () -> Unit = {},
    onNavigateToJam: () -> Unit = {},
    onNavigateToTrivia: () -> Unit = {},
    onGeneratePodcast: () -> Unit = {},
    onGenerateAudiobook: () -> Unit = {},
    hasPermissions: Boolean = false,
    onAcknowledgePermissions: () -> Unit = {},
    onTrackClick: (String) -> Unit
) {
    val tracks by viewModel.tracks.collectAsStateWithLifecycle()
    val communityTracks by viewModel.communityTracks.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val audiobooks by viewModel.audiobooks.collectAsStateWithLifecycle()
    val podcasts by viewModel.podcasts.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.catalogErrorMessage.collectAsStateWithLifecycle()

    var homeSections by remember { mutableStateOf(emptyList<com.musically.studio.dataconnect.ListHomeSectionsQuery.Data.HomeSectionsItem>()) }

    LaunchedEffect(Unit) {
        viewModel.fetchUserTracks()
        viewModel.fetchCommunityTracks()
        viewModel.fetchPodcasts()
        viewModel.fetchAudiobooks()
        try {
            val result = com.musically.studio.dataconnect.DefaultConnector.instance.listHomeSections.execute()
            homeSections = result.data.homeSections
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    LaunchedEffect(hasPermissions) {
        if (!hasPermissions) {
            onAcknowledgePermissions()
        }
    }

    var chatInputValue by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = com.musically.studio.ui.theme.MaveBackground,
        topBar = {
            MaveHomeTopBar(
                photoUrl = viewModel.getUserPhotoUrl(),
                displayName = viewModel.getUserDisplayName(),
                onNavigateToProfile = onNavigateToProfile
            )
        },
    ) { paddingValues ->
        AnimatedContent(
            targetState = when {
                isLoading && tracks.isEmpty() && communityTracks.isEmpty() -> "LOADING"
                errorMessage != null && tracks.isEmpty() && communityTracks.isEmpty() -> "ERROR"
                else -> "SUCCESS"
            },
            label = "HomeScreenState",
            modifier = Modifier.padding(paddingValues)
        ) { state ->
            when (state) {
                "LOADING" -> HomeLoadingOrganism(modifier = Modifier)
                "ERROR" -> HomeErrorOrganism(
                    errorMessage = errorMessage,
                    onRetry = {
                        viewModel.clearCatalogError()
                        viewModel.fetchUserTracks()
                        viewModel.fetchCommunityTracks()
                    }
                )
                "SUCCESS" -> HomeSuccessOrganism(
                    isLoading = isLoading,
                    onRefresh = {
                        viewModel.fetchUserTracks()
                        viewModel.fetchCommunityTracks()
                    },
                    homeSections = homeSections,
                    categories = categories,
                    audiobooks = audiobooks,
                    podcasts = podcasts,
                    tracks = tracks,
                    communityTracks = communityTracks,
                    onNavigateToMusic = onNavigateToMusic,
                    onNavigateToPodcast = onNavigateToPodcast,
                    onNavigateToAudiobooks = onNavigateToAudiobooks,
                    onNavigateToConcerts = onNavigateToConcerts,
                    onNavigateToJam = onNavigateToJam,
                    onNavigateToTrivia = onNavigateToTrivia,
                    onCategoryClick = { category ->
                        viewModel.sendTextCommand("Generate a song for category $category")
                        viewModel.navigateTo(com.musically.studio.ui.navigation.Route.LiveSession)
                    },
                    onTrackClick = onTrackClick
                )
            }
        }
    }
}
