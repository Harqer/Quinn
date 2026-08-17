/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: Android Component for UserSongsSection.kt
 */

package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.components.TrackItem

fun LazyGridScope.userSongsSection(
    isOwnProfile: Boolean,
    isLoading: Boolean,
    vibes: List<MaveTrack>,
    onPlayTrack: (MaveTrack) -> Unit,
    onNavigateToAlbum: (String) -> Unit
) {
    item(span = { GridItemSpan(maxLineSpan) }) {
        Text(
            text = if (isOwnProfile) "My Songs" else "Public Songs",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
    }

    if (isLoading && vibes.isEmpty()) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else if (vibes.isEmpty()) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "No public songs found.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        items(vibes, key = { it.id }) { track ->
            TrackItem(
                track = track,
                onClick = { onPlayTrack(track) },
                onAlbumClick = { onNavigateToAlbum(track.album.id) }
            )
        }
    }
}
