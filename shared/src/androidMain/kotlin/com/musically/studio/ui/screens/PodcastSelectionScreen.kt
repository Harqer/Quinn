package com.musically.studio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PodcastSelectionScreen(
    viewModel: MainViewModel,
    onDone: () -> Unit
) {
    val tracks by viewModel.tracks.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    val selectedPodcasts = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        viewModel.fetchUserTracks()
    }

    val filteredTracks = if (searchQuery.isBlank()) {
        tracks
    } else {
        tracks.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        containerColor = Color(0xFF121414),
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF121414).copy(alpha = 0.9f))
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDone,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                ) {
                    Text("Done", fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Now choose some\npodcasts.",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (tracks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tracks found", color = Color.White, style = MaterialTheme.typography.titleMedium)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding() + 16.dp)
                ) {
                    items(filteredTracks) { track ->
                        PodcastTile(
                            track = track,
                            isSelected = selectedPodcasts.contains(track.id),
                            onClick = {
                                if (selectedPodcasts.contains(track.id)) selectedPodcasts.remove(track.id)
                                else selectedPodcasts.add(track.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PodcastTile(track: MaveTrack, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.aspectRatio(1f)) {
            AsyncImage(
                model = track.album.images.firstOrNull()?.url,
                contentDescription = track.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray, shape = MaterialTheme.shapes.small)
            )
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = track.name,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 2
        )
    }
}
