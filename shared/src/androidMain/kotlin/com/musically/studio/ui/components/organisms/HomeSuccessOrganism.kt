package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.components.molecules.MaveCard
import com.musically.studio.ui.components.molecules.MediaCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSuccessOrganism(
    isLoading: Boolean,
    onRefresh: () -> Unit,
    homeSections: List<com.musically.studio.dataconnect.ListHomeSectionsQuery.Data.HomeSectionsItem>,
    categories: List<com.musically.studio.network.MaveCategory>,
    audiobooks: List<com.musically.studio.network.MaveAudiobook>,
    podcasts: List<com.musically.studio.network.MavePodcast>,
    tracks: List<com.musically.studio.network.MaveTrack>,
    communityTracks: List<com.musically.studio.network.MaveTrack>,
    onNavigateToMusic: () -> Unit,
    onNavigateToPodcast: () -> Unit,
    onNavigateToAudiobooks: () -> Unit,
    onNavigateToConcerts: () -> Unit,
    onNavigateToJam: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onTrackClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                val chips = homeSections.map { section ->
                    val onClickAction = when (section.route.lowercase()) {
                        "music" -> onNavigateToMusic
                        "podcasts" -> onNavigateToPodcast
                        "audiobooks" -> onNavigateToAudiobooks
                        "concerts" -> onNavigateToConcerts
                        else -> onNavigateToMusic
                    }
                    com.musically.studio.ui.components.molecules.SectionChip(section.title, onClick = onClickAction)
                }.toMutableList().apply {
                    add(0, com.musically.studio.ui.components.molecules.SectionChip("Jam Mode", onClick = onNavigateToJam))
                }
                com.musically.studio.ui.components.molecules.SectionChipsRow(chips = chips)
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                CategoryCardsRow(
                    categories = categories,
                    onCategoryClick = onCategoryClick
                )
            }

            if (audiobooks.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Audiobooks",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                items(audiobooks, key = { it.hashCode() }) { audiobook ->
                    MediaCard(
                        title = audiobook.title,
                        subtitle = audiobook.author,
                        imageUrl = audiobook.imageUrl,
                        onClick = onNavigateToAudiobooks
                    )
                }
            }

            if (podcasts.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Podcasts",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                items(podcasts, key = { it.hashCode() }) { podcast ->
                    MediaCard(
                        title = podcast.name,
                        subtitle = podcast.publisher,
                        imageUrl = podcast.imageUrl,
                        onClick = onNavigateToPodcast
                    )
                }
            }

            val recentTracks = (if (tracks.isNotEmpty()) tracks else communityTracks).take(6)
            if (recentTracks.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Recent Tracks",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    RecentTracksGrid(
                        tracks = recentTracks,
                        onTrackClick = onTrackClick
                    )
                }
            }

            val madeForYouTracks = if (tracks.isNotEmpty()) tracks.take(5) else communityTracks.take(5)
            if (madeForYouTracks.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Made for you",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                items(madeForYouTracks, key = { it.id }) { track ->
                    MaveCard(
                        track = track,
                        onClick = { onTrackClick(track.id) }
                    )
                }
            }

            if (communityTracks.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Community Songs",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                items(communityTracks, key = { it.id }) { track ->
                    MaveCard(
                        track = track,
                        onClick = { onTrackClick(track.id) }
                    )
                }
            }

            if (tracks.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Recently played",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                items(tracks, key = { it.id }) { track ->
                    MaveCard(
                        track = track,
                        onClick = { onTrackClick(track.id) }
                    )
                }
            }
        }
    }
}
