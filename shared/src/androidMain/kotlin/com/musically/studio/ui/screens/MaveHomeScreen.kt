package com.musically.studio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.MainViewModel
import java.util.Calendar

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
    
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 18 -> "Good afternoon"
        else -> "Good evening"
    }

    LaunchedEffect(Unit) {
        viewModel.fetchUserTracks()
        viewModel.fetchCommunityTracks()
    }

    Scaffold(
        containerColor = com.musically.studio.ui.theme.MaveBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val photoUrl = viewModel.getUserPhotoUrl()
                        if (photoUrl != null) {
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = "Profile",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .clickable { onNavigateToProfile() }
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(com.musically.studio.ui.theme.MaveBrand)
                                    .clickable { onNavigateToProfile() },
                                contentAlignment = Alignment.Center
                            ) {
                                val initial = viewModel.getUserDisplayName()?.firstOrNull()?.toString()?.uppercase() ?: "M"
                                Text(
                                    text = initial,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "$greeting",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = com.musically.studio.ui.theme.MaveBackground.copy(alpha = 0.9f)
                )
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
                // Recent Grid
                val recentTracks = (if (tracks.isNotEmpty()) tracks else communityTracks).take(6)
                if (recentTracks.isNotEmpty()) {
                    item {
                        RecentTracksGrid(
                            tracks = recentTracks,
                            onTrackClick = onTrackClick
                        )
                    }
                }

                // Made for you Carousel
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

                // Community Vibes Carousel
                if (communityTracks.isNotEmpty()) {
                    item {
                        MaveCarousel(
                            title = "Community Songs",
                            tracks = communityTracks,
                            onTrackClick = onTrackClick
                        )
                    }
                }

                // Recently Played Carousel (only if user has tracks)
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

@Composable
fun RecentTracksGrid(
    tracks: List<MaveTrack>,
    onTrackClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val rows = tracks.chunked(2)
        rows.forEach { rowTracks ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowTracks.forEach { track ->
                    RecentTrackItem(
                        track = track,
                        modifier = Modifier.weight(1f),
                        onClick = { onTrackClick(track.id) }
                    )
                }
                // Fill empty space if odd number
                if (rowTracks.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun RecentTrackItem(
    track: MaveTrack,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(com.musically.studio.ui.theme.MaveSurfaceContainer)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val imageUrl = track.album.images.firstOrNull()?.url
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = track.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(56.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.DarkGray)
            )
        }
        Text(
            text = track.name,
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
fun MaveCarousel(
    title: String,
    tracks: List<MaveTrack>,
    onTrackClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(tracks) { track ->
                MaveCard(
                    track = track,
                    onClick = { onTrackClick(track.id) }
                )
            }
        }
    }
}

@Composable
fun MaveCard(
    track: MaveTrack,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick)
    ) {
        val imageUrl = track.album.images.firstOrNull()?.url
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.DarkGray)
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = track.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = track.name,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = track.artists.firstOrNull()?.name ?: "",
            color = com.musically.studio.ui.theme.MaveGray300,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
