package com.musically.studio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.*

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
    val trackProgress by viewModel.trackProgress.collectAsState()

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
        containerColor = Color.Black // Dark background for better contrast
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            val actualLyrics = lyrics ?: "Generating lyrics with Gemini Flash..."
            val words = actualLyrics.split(Regex("\\s+"))
            
            // Assume 180 seconds average song length for demo if duration is unknown
            // trackProgress is in seconds. Let's map it so every word takes 0.5 seconds
            val estimatedWordCountProgress = (trackProgress * 2).toInt()
            val highlightedCount = estimatedWordCountProgress.coerceIn(0, words.size)
            
            Text(
                text = buildAnnotatedString {
                    words.forEachIndexed { index, word ->
                        if (index < highlightedCount) {
                            withStyle(style = SpanStyle(color = Color.White)) {
                                append("$word ")
                            }
                        } else {
                            withStyle(style = SpanStyle(color = Color.DarkGray)) {
                                append("$word ")
                            }
                        }
                    }
                },
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                lineHeight = MaterialTheme.typography.headlineLarge.lineHeight * 1.2f
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
