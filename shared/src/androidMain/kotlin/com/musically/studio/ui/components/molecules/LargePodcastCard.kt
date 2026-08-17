/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: Android Component for LargePodcastCard.kt
 */

package com.musically.studio.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.theme.MaveStyles
import com.musically.studio.ui.utils.debouncedClickable

@Composable
fun LargePodcastCard(
    track: MaveTrack,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    style: Style = Style
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource) { it.isEnabled = true }
    Column(
        modifier = modifier
            .width(240.dp)
            .debouncedClickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .styleable(styleState, MaveStyles.largePodcastCardStyle, style)
    ) {
        Box(modifier = Modifier.aspectRatio(3f/4f)) {
            AsyncImage(
                model = track.album.images.firstOrNull()?.url,
                contentDescription = track.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small)
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
