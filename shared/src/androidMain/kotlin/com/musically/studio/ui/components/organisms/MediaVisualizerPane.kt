package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.components.molecules.SeamlessVideoPlayer

@Composable
fun MediaVisualizerPane(
    currentVideoUrl: String?,
    currentCoverUrl: String?,
    track: MaveTrack,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.aspectRatio(1f)) {
        if (currentVideoUrl != null) {
            SeamlessVideoPlayer(
                videoUrl = currentVideoUrl,
                modifier = Modifier.fillMaxSize().clip(MaterialTheme.shapes.medium)
            )
        } else {
            AsyncImage(
                model = currentCoverUrl ?: track.album.images.firstOrNull()?.url,
                contentDescription = "Album Art",
                contentScale = ContentScale.Crop,
                placeholder = androidx.compose.ui.graphics.painter.ColorPainter(Color.DarkGray),
                error = androidx.compose.ui.graphics.painter.ColorPainter(Color.DarkGray),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
        }
    }
}
