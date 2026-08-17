/**
 * @AtomicLevel: Organism
 * @SemanticPurpose: Android Component for CompactNowPlaying.kt
 */

package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.background
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.models.AudioDevice
import com.musically.studio.ui.theme.MaveBrand
import com.musically.studio.ui.components.atoms.PlaybackControls
import com.musically.studio.ui.components.atoms.PlaybackSlider
import com.musically.studio.ui.components.atoms.VolumeSlider
import com.musically.studio.ui.components.molecules.CompactArtDisplay
import com.musically.studio.ui.components.molecules.CompactTrackHeader
import com.musically.studio.ui.components.molecules.MediaFooterDeviceRow
import com.musically.studio.ui.components.molecules.MediaVisualActionsRow
import kotlin.math.abs
import com.musically.studio.ui.utils.debouncedClickable

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
        
        CompactArtDisplay(
            currentCoverUrl = currentCoverUrl,
            currentVideoUrl = currentVideoUrl,
            fallbackCoverUrl = track.album.images.firstOrNull()?.url
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        MediaVisualActionsRow(
            onRequestCover = onRequestCover,
            onRequestVideo = onRequestVideo
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        CompactTrackHeader(
            track = track,
            onLike = onLike,
            onBookmark = onBookmark,
            onMore = onMore
        )
        
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
        
        MediaFooterDeviceRow(
            devices = devices,
            onDeviceClick = onDeviceClick,
            onShare = onShare,
            onQueueClick = onQueueClick
        )

        Spacer(modifier = Modifier.height(16.dp))
        val lyricsColor = remember(track.id) {
            val hash = abs(track.id.hashCode() * 31)
            val r = (hash and 0xFF0000) shr 16
            val g = (hash and 0x00FF00) shr 8
            val b = hash and 0x0000FF
            Color(r, g, b).copy(alpha = 0.8f)
        }
        
        // Lyrics
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(lyricsColor)
                .semantics(mergeDescendants = true) {}
                .debouncedClickable { onLyricsClick() }
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
