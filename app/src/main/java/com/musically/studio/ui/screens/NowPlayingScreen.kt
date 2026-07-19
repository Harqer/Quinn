package com.musically.studio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.musically.studio.network.SpotifyTrack
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.theme.SpotifyBlack
import com.musically.studio.ui.theme.SpotifyGreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    track: SpotifyTrack?,
    viewModel: MainViewModel,
    onCollapse: () -> Unit
) {
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpotifyBlack)
    ) {
        if (track == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = SpotifyGreen)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar equivalent
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onCollapse) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Close", tint = Color.White)
                    }
                    Text(
                        "Now Playing from your Studio", 
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(48.dp)) // balance center
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Album Art
                AsyncImage(
                    model = track.album?.images?.firstOrNull()?.url ?: "https://via.placeholder.com/400",
                    contentDescription = "Album Art",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.DarkGray)
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Track Info & Like Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = track.artists?.joinToString { it.name } ?: "Unknown Artist",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.LightGray,
                            maxLines = 1
                        )
                    }
                    IconButton(onClick = { viewModel.bookmarkTrack(track.id) }) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Like", tint = Color.White)
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Progress Bar Placeholder
                Slider(
                    value = 0.3f,
                    onValueChange = {},
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.DarkGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("1:04", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text("3:42", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Playback Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* Shuffle */ }) {
                        Icon(Icons.Default.Shuffle, contentDescription = "Shuffle", tint = Color.White)
                    }
                    IconButton(onClick = { /* Skip Previous */ }) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(SpotifyGreen, CircleShape)
                            .clickable { viewModel.togglePlayPause() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = SpotifyBlack,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    
                    IconButton(onClick = { /* Skip Next */ }) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                    IconButton(onClick = { /* Repeat */ }) {
                        Icon(Icons.Default.Repeat, contentDescription = "Repeat", tint = Color.White)
                    }
                }
            }
        }
    }
}
