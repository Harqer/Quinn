package com.musically.studio.ui.components.organisms

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.models.AudioDevice
import com.musically.studio.ui.components.atoms.PlaybackControls
import com.musically.studio.ui.components.atoms.PlaybackSlider
import com.musically.studio.ui.components.molecules.SeamlessVideoPlayer

@Composable
fun TwoPaneNowPlaying(
    track: MaveTrack,
    isPlaying: Boolean,
    trackProgress: Float,
    isShuffleEnabled: Boolean,
    isRepeatEnabled: String,
    currentCoverUrl: String?,
    currentVideoUrl: String?,
    devices: List<AudioDevice>,
    modality: String,
    onCollapse: () -> Unit,
    onMore: () -> Unit,
    onSeek: (Float) -> Unit,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onLike: () -> Unit,
    onBookmark: () -> Unit,
    onShare: () -> Unit,
    onQueueClick: () -> Unit,
    onLyricsClick: () -> Unit,
    onDeviceClick: () -> Unit,
    onRequestCover: () -> Unit,
    onRequestVideo: () -> Unit,
    volume: Float,
    onVolumeChange: (Float) -> Unit
) {
    val localCtx = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Pane: Album Art / Video
        Box(modifier = Modifier.weight(1f).aspectRatio(1f)) {
            if (currentVideoUrl != null) {
                SeamlessVideoPlayer(
                    videoUrl = currentVideoUrl,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                )
            } else {
                AsyncImage(
                    model = currentCoverUrl ?: track.album.images.firstOrNull()?.url,
                    contentDescription = "Album Art",
                    contentScale = ContentScale.Crop,
                    placeholder = androidx.compose.ui.graphics.painter.ColorPainter(Color.DarkGray),
                    error = androidx.compose.ui.graphics.painter.ColorPainter(Color.DarkGray),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
            }
        }

        // Right Pane: Details & Controls
        Column(
            modifier = Modifier.weight(1.2f),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCollapse) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Close")
                }
                Text(
                    text = when(modality) {
                        "podcast" -> "Mave Podcast"
                        "audiobook" -> "Mave Audiobook"
                        else -> "Unknown Artist"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1
                    )
                    Text(
                        text = track.artists.joinToString { it.name },
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                Row {
                    IconButton(onClick = onLike) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Like", tint = MaterialTheme.colorScheme.onBackground)
                    }
                    IconButton(onClick = onBookmark) {
                        Icon(Icons.Default.BookmarkBorder, contentDescription = "Bookmark", tint = MaterialTheme.colorScheme.onBackground)
                    }
                    IconButton(onClick = onMore) {
                        Icon(Icons.Default.MoreHoriz, contentDescription = "More", tint = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            PlaybackSlider(
                progress = trackProgress,
                durationMs = track.durationMs,
                onSeek = onSeek
            )

            Spacer(modifier = Modifier.height(32.dp))

            PlaybackControls(
                isPlaying = isPlaying,
                isShuffleEnabled = isShuffleEnabled,
                isRepeatEnabled = isRepeatEnabled,
                onToggleShuffle = onToggleShuffle,
                onToggleRepeat = onToggleRepeat,
                onPrevious = onPrevious,
                onNext = onNext,
                onPlayPause = onTogglePlay
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onRequestCover,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f))
                ) {
                    Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("New Cover", style = MaterialTheme.typography.labelMedium)
                }
                Button(
                    onClick = onRequestVideo,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f))
                ) {
                    Icon(Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Music Video", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
