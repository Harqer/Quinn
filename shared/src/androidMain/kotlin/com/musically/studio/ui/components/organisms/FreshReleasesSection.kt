/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: Android Component for FreshReleasesSection.kt
 */

package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items as listItems
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.components.molecules.LargePodcastCard
import com.musically.studio.ui.components.atoms.MaveButton

@Composable
fun FreshReleasesSection(
    modifier: Modifier = Modifier,
    communityTracks: List<MaveTrack>,
    isLoading: Boolean = false,
    errorMessage: String?,
    onRetry: () -> Unit,
    onNavigateToMore: () -> Unit,
    onNavigateToTrack: (String) -> Unit
) {
    Column(modifier = modifier.padding(bottom = 32.dp)) {
        PaddingValues(horizontal = 24.dp).let {
            Row(
                modifier = Modifier.fillMaxWidth().padding(it).padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fresh Releases",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                TextButton(onClick = onNavigateToMore) {
                    Text("More >", color = Color.Gray)
                }
            }
        }

        if (isLoading && communityTracks.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                androidx.compose.material3.CircularProgressIndicator(color = Color.White)
            }
        } else if (errorMessage != null && communityTracks.isEmpty()) {
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
        } else if (communityTracks.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                Text("No tracks found", color = Color.White, style = MaterialTheme.typography.titleMedium)
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                listItems(communityTracks, key = { it.id }) { track ->
                    LargePodcastCard(track = track, onClick = { 
                        onNavigateToTrack(track.id)
                    })
                }
            }
        }
    }
}
