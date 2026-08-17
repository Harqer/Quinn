/**
 * @AtomicLevel: Atom
 * @SemanticPurpose: Android Component for RecentTracksGrid.kt
 */

package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.components.molecules.RecentTrackItem

@Composable
fun RecentTracksGrid(
    tracks: List<MaveTrack>,
    onTrackClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val rows = tracks.chunked(2)
        rows.forEach { rowTracks ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowTracks.forEach { track ->
                    RecentTrackItem(
                        track = track,
                        modifier = Modifier.weight(1f),
                        onClick = { onTrackClick(track.id) }
                    )
                }
                // Fill empty space if odd number
                if (rowTracks.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
