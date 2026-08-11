package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.components.molecules.GlassmorphicControlsRow
import com.musically.studio.ui.components.molecules.GlassmorphicHeaderRow

@Composable
fun GlassmorphicPlayer(
    track: MaveTrack?,
    isPlaying: Boolean,
    progress: Float,
    onPlayPauseClick: () -> Unit,
    onEqClick: (() -> Unit)? = null,
    onCloseClick: () -> Unit,
    onUndoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val durationMs = track?.durationMs ?: 0L
    val durationSec = durationMs / 1000
    val progressSec = progress.toLong() / 1000
    val remainingSec = maxOf(0, durationSec - progressSec)
    val progressRatio = if (durationMs > 0L) (progress / durationMs).coerceIn(0f, 1f) else 0f

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    GlassmorphicHeaderRow(
                        track = track,
                        onCloseClick = onCloseClick
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.1f))
                    )
                    
                    GlassmorphicControlsRow(
                        isPlaying = isPlaying,
                        progressSec = progressSec,
                        remainingSec = remainingSec,
                        progressRatio = progressRatio,
                        onPlayPauseClick = onPlayPauseClick,
                        onUndoClick = onUndoClick,
                        onEqClick = onEqClick
                    )
                }
            }
        }
    }
}
