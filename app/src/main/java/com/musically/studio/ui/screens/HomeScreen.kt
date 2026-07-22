package com.musically.studio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.theme.MaveBrand

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    isWearableConnected: Boolean = false,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToLibrary: () -> Unit = {},
    onNavigateToDevices: () -> Unit = {},
    onNavigateToMore: () -> Unit = {},
    onNavigateToCamera: () -> Unit = {},
    onNavigateToLiveSession: () -> Unit = {}
) {
    val communityTracks by viewModel.communityTracks.collectAsStateWithLifecycle()
    val userTracks by viewModel.tracks.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchCommunityTracks()
        viewModel.fetchUserTracks()
    }

    Scaffold(
        containerColor = Color(0xFF121414),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateToSettings) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                }
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = Color(0xFF1E2020)
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        Button(
                            onClick = { viewModel.fetchCommunityTracks() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37393A), contentColor = Color.White)
                        ) {
                            Text("Discover", fontWeight = FontWeight.Bold)
                        }
                        TextButton(onClick = onNavigateToLibrary) {
                            Text("Library", color = Color.Gray, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF121414).copy(alpha = 0.9f))
                    .padding(24.dp)
                    .navigationBarsPadding()
            ) {
                com.musically.studio.ui.components.atoms.MaveTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = "Search for more podcasts...",
                    trailingIcon = { 
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onNavigateToCamera) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = "Camera", tint = Color.Gray)
                            }
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) 
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                com.musically.studio.ui.components.atoms.MaveButton(
                    text = "Start Listening",
                    onClick = onNavigateToLiveSession,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Column(modifier = Modifier.padding(bottom = 32.dp)) {
                    PaddingValues(horizontal = 24.dp).let {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(it).padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Fresh from v4",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            TextButton(onClick = onNavigateToMore) {
                                Text("More >", color = Color.Gray)
                            }
                        }
                    }

                    if (communityTracks.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            Text("No tracks found", color = Color.White, style = MaterialTheme.typography.titleMedium)
                        }
                    } else {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(communityTracks) { track ->
                                LargePodcastCard(track = track)
                            }
                        }
                    }
                }
            }

            item {
                PaddingValues(horizontal = 24.dp).let {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(it).padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Global Trending",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        TextButton(onClick = onNavigateToMore) {
                            Text("More >", color = Color.Gray)
                        }
                    }
                }
            }

            if (userTracks.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("No tracks found", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    }
                }
            } else {
                items(userTracks) { track ->
                    TrendingItemRow(track = track)
                }
            }
        }
    }
}

@Composable
fun LargePodcastCard(track: MaveTrack) {
    Column(modifier = Modifier.width(240.dp)) {
        Box(modifier = Modifier.aspectRatio(3f/4f)) {
            AsyncImage(
                model = track.album.images.firstOrNull()?.url,
                contentDescription = track.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().background(Color.DarkGray, shape = MaterialTheme.shapes.small)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = track.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1
        )
        Text(
            text = track.artists.joinToString { it.name },
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            maxLines = 1
        )
    }
}

@Composable
fun TrendingItemRow(track: MaveTrack) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.album.images.firstOrNull()?.url,
            contentDescription = track.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(80.dp).background(Color.DarkGray, shape = MaterialTheme.shapes.small)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = track.artists.joinToString { it.name },
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1
            )
        }
    }
}
