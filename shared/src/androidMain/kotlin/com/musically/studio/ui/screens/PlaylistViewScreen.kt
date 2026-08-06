package com.musically.studio.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Brush
import coil.compose.AsyncImage
import kotlin.math.abs
import timber.log.Timber
import com.musically.studio.shared.R
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.*
import com.musically.studio.ui.components.organisms.PlaylistHeader
import com.musically.studio.ui.components.molecules.PlaylistTrackItem
import com.musically.studio.ui.theme.FormFactorPreviews

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistViewScreen(
    playlistId: String,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onTrackClick: (String) -> Unit,
    onMoreClick: (String) -> Unit,
    onLikeClick: () -> Unit = {},
    onDownloadClick: () -> Unit = {},
    onRemixClick: () -> Unit = {}
) {
    val tracks by viewModel.tracks.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val likedTracks by viewModel.likedTracks.collectAsState()
    val context = LocalContext.current
    
    // Get tracks from playlist or fallback to empty
    val playlistInfo = playlists.firstOrNull { it.id == playlistId }
    val playlistTracks = playlistInfo?.tracks ?: emptyList()
    val isLiked = playlistTracks.isNotEmpty() && playlistTracks.all { t -> likedTracks.any { it.id == t.id } }

    val bgColor = remember(playlistId) {
        val hash = abs(playlistId.hashCode())
        val r = (hash and 0xFF0000) shr 16
        val g = (hash and 0x00FF00) shr 8
        val b = hash and 0x0000FF
        Color(r, g, b).copy(alpha = 0.5f)
    }

    val totalDurationMs = playlistTracks.sumOf { it.durationMs }
    val formattedDuration = if (totalDurationMs > 0) {
        val mins = totalDurationMs / 60000
        val hours = mins / 60
        if (hours > 0) "${hours}h ${mins % 60}m" else "${mins}m"
    } else ""
    val formattedLikes = "%,d".format(playlistInfo?.likes ?: 0)
    


    Scaffold(
        containerColor = com.musically.studio.ui.theme.MaveBackground,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Gradient Header for Playlist (using a generic green/dark gradient)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(bgColor, com.musically.studio.ui.theme.MaveBackground)
                        )
                    )
            )
            LazyVerticalGrid(
                columns = GridCells.Adaptive(300.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = paddingValues
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    PlaylistHeader(
                        title = playlistInfo?.name ?: "Playlist",
                        subtitle = "Mave Community",
                        description = playlistInfo?.description,
                        imageUrl = playlistInfo?.coverUrl ?: playlistTracks.firstOrNull()?.album?.images?.firstOrNull()?.url,
                        playlistId = playlistId,
                        context = context,
                        isLiked = isLiked,
                        onLikeClick = onLikeClick,
                        onDownloadClick = onDownloadClick,
                        onMoreClick = { playlistTracks.firstOrNull()?.let { onMoreClick(it.id) } },
                        onPlayClick = { playlistTracks.firstOrNull()?.let { onTrackClick(it.id) } },
                        onRemixClick = onRemixClick
                    )
                }

                itemsIndexed(playlistTracks) { index, track ->
                    PlaylistTrackItem(
                        track = track,
                        trackNumber = index + 1,
                        onClick = { onTrackClick(track.id) },
                        onMoreClick = {
                            onMoreClick(track.id)
                        }
                    )
                }
            }
        }
    }
}



@FormFactorPreviews
@Composable
fun PlaylistViewScreenPreview() {
    MaterialTheme {
        PlaylistViewScreen(
            playlistId = "123e4567-e89b-12d3-a456-426614174000",
            viewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
            onNavigateBack = {},
            onTrackClick = {},
            onMoreClick = {}
        )
    }
}
