package com.musically.studio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricsBottomSheet(
    trackId: String?,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val tracks by viewModel.tracks.collectAsState()
    val track = tracks.find { it.id == trackId }
    val lyrics by viewModel.lyrics.collectAsState()

    androidx.compose.runtime.LaunchedEffect(trackId, track?.audioUrl) {
        if (trackId != null) {
            val audioUrl = track?.audioUrl
            if (audioUrl != null) {
                viewModel.generateLyrics(trackId, audioUrl)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Lyrics - ${track?.name ?: "Unknown"}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = lyrics ?: "Generating lyrics with Gemini Flash...",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
