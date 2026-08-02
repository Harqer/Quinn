package com.musically.studio.ui.components.organisms

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.models.AudioDevice
import com.musically.studio.ui.theme.MaveBrand
import com.musically.studio.ui.components.atoms.PlaybackControls
import com.musically.studio.ui.components.atoms.PlaybackSlider
import com.musically.studio.ui.components.atoms.VolumeSlider
import com.musically.studio.ui.components.molecules.SeamlessVideoPlayer
import kotlin.math.abs

@Composable
fun CompactNowPlaying(
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCollapse) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Close", tint = MaterialTheme.colorScheme.onBackground)
            }
            Text(
                when(modality) {
                    "podcast" -> "Mave Podcast"
                    "audiobook" -> "Mave Audiobook"
                    else -> "Unknown Artist"
                }, 
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(48.dp))
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Album Art / Video
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
            if (currentVideoUrl != null) {
                SeamlessVideoPlayer(
                    videoUrl = currentVideoUrl,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))
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
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AssistChip(
                onClick = onRequestCover,
                label = { Text("Regenerate Cover") },
                leadingIcon = { Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            AssistChip(
                onClick = onRequestVideo,
                label = { Text("Generate Video") },
                leadingIcon = { Icon(Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Track Info & Like Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = track.artists.joinToString { it.name },
                    style = MaterialTheme.typography.bodyLarge,
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
        
        PlaybackSlider(progress = trackProgress, durationMs = track.durationMs, onSeek = onSeek)
        
        Spacer(modifier = Modifier.height(24.dp))
        
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
        
        Spacer(modifier = Modifier.height(16.dp))
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
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onDeviceClick() }
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.Bluetooth, contentDescription = "Device", tint = MaveBrand, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(devices.firstOrNull()?.name ?: "Phone Speaker", color = MaveBrand, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
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
        
        Spacer(modifier = Modifier.weight(1f))
        
        val lyricsColor = remember(track.id) {
            val hash = abs(track.id.hashCode() * 31)
            val r = (hash and 0xFF0000) shr 16
            val g = (hash and 0x00FF00) shr 8
            val b = hash and 0x0000FF
            Color(r, g, b).copy(alpha = 0.8f)
        }
        
        // Lyrics Peek
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(lyricsColor)
                .clickable { onLyricsClick() }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(4.dp)
                    .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Lyrics", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Box(
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(16.dp)).padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("MORE", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
