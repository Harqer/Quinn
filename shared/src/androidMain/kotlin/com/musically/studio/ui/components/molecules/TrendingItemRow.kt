/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: Android Component for TrendingItemRow.kt
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
import androidx.compose.ui.Alignment
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
fun TrendingItemRow(
    track: MaveTrack,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    style: Style = Style
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource) { it.isEnabled = true }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .debouncedClickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .styleable(styleState, MaveStyles.listRowItemStyle, style),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.album.images.firstOrNull()?.url,
            contentDescription = track.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(80.dp).background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small)
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
