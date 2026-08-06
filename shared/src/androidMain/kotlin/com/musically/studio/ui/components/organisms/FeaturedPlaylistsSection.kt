package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.network.MavePlaylist
import com.musically.studio.ui.components.atoms.MaveButton
import com.musically.studio.ui.components.molecules.PlaylistRowItem

fun LazyGridScope.featuredPlaylistsSection(
    playlists: List<MavePlaylist>,
    isLoading: Boolean = false,
    errorMessage: String?,
    onRetry: () -> Unit,
    onNavigateToMore: () -> Unit,
    onNavigateToPlaylist: (String) -> Unit
) {
    item(span = { GridItemSpan(maxLineSpan) }) {
        PaddingValues(horizontal = 24.dp).let {
            Row(
                modifier = Modifier.fillMaxWidth().padding(it).padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Featured Playlists",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                TextButton(onClick = onNavigateToMore) {
                    Text("More >", color = Color.Gray)
                }
            }
        }
    }

    if (isLoading && playlists.isEmpty()) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                androidx.compose.material3.CircularProgressIndicator(color = Color.White)
            }
        }
    } else if (errorMessage != null && playlists.isEmpty()) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    MaveButton(
                        text = "Retry",
                        onClick = onRetry
                    )
                }
            }
        }
    } else if (playlists.isEmpty()) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                Text("No playlists found", color = Color.White, style = MaterialTheme.typography.titleMedium)
            }
        }
    } else {
        gridItems(playlists, key = { it.id }) { playlist ->
            PlaylistRowItem(playlist = playlist, onClick = { 
                onNavigateToPlaylist(playlist.id)
            })
        }
    }
}
