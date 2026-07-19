package com.musically.studio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.musically.studio.network.SpotifyTrack
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.theme.SpotifyBlack
import com.musically.studio.ui.theme.SpotifyGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumViewScreen(
    albumId: String,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onTrackClick: (String) -> Unit
) {
    // For now, filtering user tracks by this album ID if it exists, or just showing generic info
    val tracks by viewModel.tracks.collectAsState()
    // Mocking an album view using the first track's album as the display
    val albumTracks = tracks.filter { it.album?.id == albumId }
    val albumInfo = albumTracks.firstOrNull()?.album

    Scaffold(
        containerColor = SpotifyBlack,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = albumInfo?.images?.firstOrNull()?.url ?: "https://via.placeholder.com/200",
                        contentDescription = "Album Art",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.DarkGray)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = albumInfo?.name ?: "Unknown Album",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Album • ${albumInfo?.artists?.joinToString { it.name } ?: "Unknown Artist"}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.LightGray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            IconButton(onClick = { albumTracks.firstOrNull()?.let { viewModel.bookmarkTrack(it.id) } }) {
                                Icon(Icons.Default.FavoriteBorder, contentDescription = "Like", tint = Color.White)
                            }
                            IconButton(onClick = { /* More */ }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(SpotifyGreen, CircleShape)
                                .clickable { 
                                    albumTracks.firstOrNull()?.let { onTrackClick(it.id) }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = SpotifyBlack, modifier = Modifier.size(32.dp))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            itemsIndexed(albumTracks) { index, track ->
                AlbumTrackItem(
                    track = track,
                    trackNumber = index + 1,
                    onClick = { onTrackClick(track.id) }
                )
            }
        }
    }
}

@Composable
fun AlbumTrackItem(
    track: SpotifyTrack,
    trackNumber: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = trackNumber.toString(),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.width(32.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.name,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                maxLines = 1
            )
            Text(
                text = track.artists?.joinToString { it.name } ?: "Unknown",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1
            )
        }
        IconButton(onClick = { /* More options menu to be implemented */ }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options", tint = Color.Gray)
        }
    }
}
