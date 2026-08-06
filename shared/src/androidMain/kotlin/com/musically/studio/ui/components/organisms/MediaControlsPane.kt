package com.musically.studio.ui.components.organisms
import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.models.AudioDevice
import com.musically.studio.ui.components.atoms.PlaybackControls
import com.musically.studio.ui.components.atoms.PlaybackSlider
import com.musically.studio.ui.components.atoms.VolumeSlider

@Composable
fun MediaControlsPane(
    track: MaveTrack,
    isPlaying: Boolean,
    trackProgress: Float,
    isShuffleEnabled: Boolean,
    isRepeatEnabled: String,
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
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
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

        Spacer(modifier = Modifier.height(24.dp))
        VolumeSlider(volume = volume, onVolumeChange = onVolumeChange)
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable { onDeviceClick() }
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.Bluetooth, contentDescription = "Device", tint = com.musically.studio.ui.theme.MaveBrand, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(devices.firstOrNull()?.name ?: "Phone Speaker", color = com.musically.studio.ui.theme.MaveBrand, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                IconButton(onClick = onShare, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                }
                IconButton(onClick = onQueueClick, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Menu, contentDescription = "Queue", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Lyrics
        val lyricsColor = remember(track.id) {
            val hash = kotlin.math.abs(track.id.hashCode() * 31)
            val r = (hash and 0xFF0000) shr 16
            val g = (hash and 0x00FF00) shr 8
            val b = hash and 0x0000FF
            Color(r, g, b).copy(alpha = 0.8f)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(MaterialTheme.shapes.large)
                .background(lyricsColor)
                .clickable { onLyricsClick() }
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 0.dp),
        ) {
            Text("Lyrics", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            SyncedLyricsView(
                lyrics = track.lyrics ?: emptyList(),
                currentProgressMs = (trackProgress * track.durationMs).toLong(),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
