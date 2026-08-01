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
import com.musically.studio.ui.components.organisms.AlbumHeader
import com.musically.studio.ui.components.molecules.AlbumTrackItem
import com.musically.studio.ui.theme.FormFactorPreviews

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumViewScreen(
    albumId: String,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onTrackClick: (String) -> Unit,
    onMoreClick: (String) -> Unit,
    onLikeClick: () -> Unit = {},
    onDownloadClick: () -> Unit = {}
) {
    val tracks by viewModel.tracks.collectAsState()
    val context = LocalContext.current
    val albumTracks = tracks.filter { it.album.id == albumId }
    val albumInfo = albumTracks.firstOrNull()?.album
    
    val bgColor = remember(albumId) {
        val hash = abs(albumId.hashCode())
        val r = (hash and 0xFF0000) shr 16
        val g = (hash and 0x00FF00) shr 8
        val b = hash and 0x0000FF
        Color(r, g, b).copy(alpha = 0.5f)
    }

    val totalDurationMs = albumInfo?.durationMs?.takeIf { it > 0 } ?: albumTracks.sumOf { it.durationMs }
    val formattedDuration = if (totalDurationMs > 0) {
        val mins = totalDurationMs / 60000
        val hours = mins / 60
        if (hours > 0) "${hours}h ${mins % 60}m" else "${mins}m"
    } else ""
    val formattedLikes = "%,d".format(albumInfo?.likes ?: 0)


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
            // Gradient Header
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
                    AlbumHeader(
                        title = albumInfo?.name ?: "Unknown Album",
                        subtitle = albumInfo?.description ?: albumInfo?.artists?.joinToString { it.name } ?: "Unknown Artist",
                        imageUrl = albumInfo?.images?.firstOrNull()?.url,
                        albumId = albumId,
                        context = context,
                        onLikeClick = onLikeClick,
                        onDownloadClick = onDownloadClick,
                        onMoreClick = { albumTracks.firstOrNull()?.let { onMoreClick(it.id) } },
                        onPlayClick = { albumTracks.firstOrNull()?.let { onTrackClick(it.id) } }
                    )
                }

            itemsIndexed(albumTracks) { index, track ->
                AlbumTrackItem(
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
fun AlbumViewScreenPreview() {
    MaterialTheme {
        AlbumViewScreen(
            albumId = "123e4567-e89b-12d3-a456-426614174000",
            viewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
            onNavigateBack = {},
            onTrackClick = {},
            onMoreClick = {}
        )
    }
}
