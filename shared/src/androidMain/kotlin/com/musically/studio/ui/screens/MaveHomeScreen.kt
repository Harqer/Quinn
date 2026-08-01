package com.musically.studio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.*
import com.musically.studio.ui.components.organisms.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaveHomeScreen(
    viewModel: MainViewModel,
    onNavigateToProfile: () -> Unit = {},
    onTrackClick: (String) -> Unit
) {
    val tracks by viewModel.tracks.collectAsStateWithLifecycle()
    val communityTracks by viewModel.communityTracks.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.catalogErrorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchUserTracks()
        viewModel.fetchCommunityTracks()
    }

    Scaffold(
        containerColor = com.musically.studio.ui.theme.MaveBackground,
        topBar = {
            MaveHomeTopBar(
                photoUrl = viewModel.getUserPhotoUrl(),
                displayName = viewModel.getUserDisplayName(),
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        if (isLoading && tracks.isEmpty() && communityTracks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = com.musically.studio.ui.theme.MaveBrand)
            }
        } else if (errorMessage != null && tracks.isEmpty() && communityTracks.isEmpty()) {
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
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {

                item {
                    CategoryCardsRow(
                        onCategoryClick = { category ->
                            viewModel.sendTextCommand("Generate a $category song")
                            viewModel.navigateTo(com.musically.studio.ui.navigation.Route.LiveSession)
                        }
                    )
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
