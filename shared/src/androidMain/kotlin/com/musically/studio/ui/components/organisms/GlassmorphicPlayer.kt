/**
 * @AtomicLevel: Organism
 * @SemanticPurpose: Android Component for GlassmorphicPlayer.kt
 */

package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.components.molecules.GlassmorphicControlsRow
import com.musically.studio.ui.components.molecules.GlassmorphicHeaderRow
import kotlin.math.abs

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

    val bgColor = remember(track?.id) {
        val hash = abs(track?.id?.hashCode() ?: 0)
        val r = (hash and 0xFF0000) shr 16
        val g = (hash and 0x00FF00) shr 8
        val b = hash and 0x0000FF
        Color(r, g, b)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        bgColor.copy(alpha = 0.6f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GlassmorphicHeaderRow(
                track = track,
                onCloseClick = onCloseClick
            )

            Spacer(modifier = Modifier.weight(1f))

            // Cover Art
            val imageUrl = track?.album?.images?.firstOrNull()?.url
            Image(
                painter = rememberAsyncImagePainter(imageUrl ?: ""),
                contentDescription = "Cover Art",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Track Info
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = track?.name ?: "Unknown Track",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Text(
                    text = track?.artists?.firstOrNull()?.name ?: "Unknown Artist",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            GlassmorphicControlsRow(
                isPlaying = isPlaying,
                progressSec = progressSec,
                remainingSec = remainingSec,
                progressRatio = progressRatio,
                onPlayPauseClick = onPlayPauseClick,
                onUndoClick = onUndoClick,
                onEqClick = onEqClick
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
