package com.musically.studio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.*
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

    LaunchedEffect(Unit) {
        viewModel.fetchUserTracks()
        viewModel.fetchCommunityTracks()
    }

    LaunchedEffect(hasPermissions) {
        if (!hasPermissions) {
            onAcknowledgePermissions()
        }
    }

    Scaffold(
        containerColor = com.musically.studio.ui.theme.MaveBackground,
        topBar = {
            MaveHomeTopBar(
                photoUrl = viewModel.getUserPhotoUrl(),
                displayName = viewModel.getUserDisplayName(),
                onNavigateToProfile = onNavigateToProfile
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCamera,
                shape = androidx.compose.foundation.shape.CircleShape,
                containerColor = com.musically.studio.ui.theme.MaveBrand,
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.filled.PhotoCamera,
                    contentDescription = "Open Camera",
                    tint = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
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
                "LOADING" -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = com.musically.studio.ui.theme.MaveBrand)
                    }
                }
                "ERROR" -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(errorMessage ?: "An error occurred", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(16.dp))
                            com.musically.studio.ui.components.atoms.MaveButton(
                                text = "Retry",
                                onClick = { 
                                    viewModel.clearCatalogError()
                                    viewModel.fetchUserTracks()
                                    viewModel.fetchCommunityTracks() 
                                }
                            )
                        }
                    }
                }
                "SUCCESS" -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 120.dp)
                    ) {
                item {
                    val chips = listOf(
                        com.musically.studio.ui.components.molecules.SectionChip("Music", onClick = onNavigateToMusic),
                        com.musically.studio.ui.components.molecules.SectionChip("Podcasts", onClick = onNavigateToPodcast),
                        com.musically.studio.ui.components.molecules.SectionChip("Audiobooks", onClick = onNavigateToAudiobooks),
                        com.musically.studio.ui.components.molecules.SectionChip("Concerts", onClick = onNavigateToConcerts)
                    )
                    com.musically.studio.ui.components.molecules.SectionChipsRow(chips = chips)
                }

                item {
                    CategoryCardsRow(
                        categories = categories,
                        onCategoryClick = { category ->
                            viewModel.sendTextCommand("Generate a song for category $category")
                            viewModel.navigateTo(com.musically.studio.ui.navigation.Route.LiveSession)
                        }
                    )
                }

                if (audiobooks.isNotEmpty()) {
                    item {
                        MediaCarousel(
                            title = "Audiobooks",
                            items = audiobooks,
                            key = { it.hashCode() }
                        ) { audiobook ->
                            MediaCard(
                                title = audiobook.title,
                                subtitle = audiobook.author,
                                imageUrl = audiobook.imageUrl,
                                onClick = onNavigateToAudiobooks
                            )
                        }
                    }
                }

                if (podcasts.isNotEmpty()) {
                    item {
                        MediaCarousel(
                            title = "Podcasts",
                            items = podcasts,
                            key = { it.hashCode() }
                        ) { podcast ->
                            MediaCard(
                                title = podcast.name,
                                subtitle = podcast.publisher,
                                imageUrl = podcast.imageUrl,
                                onClick = onNavigateToPodcast
                            )
                        }
                    }
                }

                val recentTracks = (if (tracks.isNotEmpty()) tracks else communityTracks).take(6)
                if (recentTracks.isNotEmpty()) {
                    item {
                        RecentTracksGrid(
                            tracks = recentTracks,
                            onTrackClick = onTrackClick
                        )
                    }
                }

                val madeForYouTracks = if (tracks.isNotEmpty()) tracks.take(5) else communityTracks.take(5)
                if (madeForYouTracks.isNotEmpty()) {
                    item {
                        MaveCarousel(
                            title = "Made for you",
                            tracks = madeForYouTracks,
                            onTrackClick = onTrackClick
                        )
                    }
                }

                if (communityTracks.isNotEmpty()) {
                    item {
                        MaveCarousel(
                            title = "Community Songs",
                            tracks = communityTracks,
                            onTrackClick = onTrackClick
                        )
                    }
                }

                if (tracks.isNotEmpty()) {
                    item {
                        MaveCarousel(
                            title = "Recently played",
                            tracks = tracks,
                            onTrackClick = onTrackClick
                        )
                    }
                }
                }
            }
        }
    }
}
}
