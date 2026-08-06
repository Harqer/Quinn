package com.musically.studio.ui.components.organisms

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.models.AudioDevice

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
        MediaVisualizerPane(
            currentVideoUrl = currentVideoUrl,
            currentCoverUrl = currentCoverUrl,
            track = track,
            modifier = Modifier.weight(1f)
        )

        // Right Pane: Details & Controls
        MediaControlsPane(
            track = track,
            isPlaying = isPlaying,
            trackProgress = trackProgress,
            isShuffleEnabled = isShuffleEnabled,
            isRepeatEnabled = isRepeatEnabled,
            devices = devices,
            modality = modality,
            onCollapse = onCollapse,
            onMore = onMore,
            onSeek = onSeek,
            onTogglePlay = onTogglePlay,
            onNext = onNext,
            onPrevious = onPrevious,
            onToggleShuffle = onToggleShuffle,
            onToggleRepeat = onToggleRepeat,
            onLike = onLike,
            onBookmark = onBookmark,
            onShare = onShare,
            onQueueClick = onQueueClick,
            onLyricsClick = onLyricsClick,
            onDeviceClick = onDeviceClick,
            onRequestCover = onRequestCover,
            onRequestVideo = onRequestVideo,
            volume = volume,
            onVolumeChange = onVolumeChange,
            modifier = Modifier.weight(1.2f)
        )
    }
}
