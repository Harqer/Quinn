/**
 * @AtomicLevel: Atom
 * @SemanticPurpose: Android Component for SearchResultsGrid.kt
 */

package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.components.TrackItem

@Composable
fun SearchResultsGrid(
    modifier: Modifier = Modifier,
    filteredResults: List<MaveTrack>,
    contentPadding: PaddingValues,
    onPlayTrack: (MaveTrack) -> Unit,
    onNavigateToAlbum: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(300.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding
    ) {
        items(filteredResults.size, key = { filteredResults[it].id }) { index ->
            val track = filteredResults[index]
            TrackItem(
                track = track,
                onClick = { onPlayTrack(track) },
                onAlbumClick = { onNavigateToAlbum(track.album.id) }
            )
        }
    }
}
