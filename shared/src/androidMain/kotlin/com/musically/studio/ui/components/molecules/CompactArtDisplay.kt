/**
 * @AtomicLevel: Atom
 * @SemanticPurpose: Android Component for CompactArtDisplay.kt
 */

package com.musically.studio.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun CompactArtDisplay(
    currentCoverUrl: String?,
    currentVideoUrl: String?,
    fallbackCoverUrl: String?,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth().aspectRatio(1f)) {
        if (currentVideoUrl != null) {
            SeamlessVideoPlayer(
                videoUrl = currentVideoUrl,
                modifier = Modifier.fillMaxSize().clip(MaterialTheme.shapes.small)
            )
        } else {
            AsyncImage(
                model = currentCoverUrl ?: fallbackCoverUrl,
                contentDescription = "Album Art",
                contentScale = ContentScale.Crop,
                placeholder = androidx.compose.ui.graphics.painter.ColorPainter(Color.DarkGray),
                error = androidx.compose.ui.graphics.painter.ColorPainter(Color.DarkGray),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
        }
    }
}
