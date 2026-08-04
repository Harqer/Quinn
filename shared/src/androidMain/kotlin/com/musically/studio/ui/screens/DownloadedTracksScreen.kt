package com.musically.studio.ui.screens

import android.os.Environment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musically.studio.network.MaveAlbum
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.playTrack
import com.musically.studio.ui.components.molecules.RecentTrackItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadedTracksScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    var downloadedTracks by remember { mutableStateOf<List<MaveTrack>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val files = downloadsDir.listFiles { _, name -> name.endsWith(".mp3") } ?: emptyArray()
            
            downloadedTracks = files.map { file ->
                MaveTrack(
                    id = file.absolutePath,
                    name = file.nameWithoutExtension.replace("_", " "),
                    artists = emptyList(),
                    album = MaveAlbum(id = "", name = "Downloads", images = emptyList()),
                    audioUrl = file.toURI().toString()
                )
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Downloaded Tracks") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (downloadedTracks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No downloaded tracks found.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                items(downloadedTracks) { track ->
                    RecentTrackItem(
                        track = track,
                        onClick = { viewModel.playTrack(track) }
                    )
                }
            }
        }
    }
}
