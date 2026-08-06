package com.musically.studio.ui.screens
import androidx.compose.material3.MaterialTheme

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.*
import com.musically.studio.ui.components.organisms.CompactNowPlaying
import com.musically.studio.ui.components.organisms.TwoPaneNowPlaying
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    track: MaveTrack?,
    viewModel: MainViewModel,
    modality: String = "music",
    onCollapse: () -> Unit,
    onMoreOptions: (String) -> Unit,
    onQueueClick: () -> Unit = {},
    onLyricsClick: () -> Unit = {},
    onDeviceClick: () -> Unit = {}
) {
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val trackProgress by viewModel.trackProgress.collectAsStateWithLifecycle()
    val isShuffleEnabled by viewModel.isShuffleEnabled.collectAsStateWithLifecycle()
    val isRepeatEnabled by viewModel.isRepeatEnabled.collectAsStateWithLifecycle()
    val currentCoverUrl by viewModel.currentCoverUrl.collectAsStateWithLifecycle()
    val currentVideoUrl by viewModel.currentVideoUrl.collectAsStateWithLifecycle()
    val devices by viewModel.devices.collectAsStateWithLifecycle()
    val volume by viewModel.volume.collectAsStateWithLifecycle()
    val localCtx = androidx.compose.ui.platform.LocalContext.current

    val adaptiveInfo = androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2()
    val isExpanded = adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(840)

    LaunchedEffect(Unit) {
        viewModel.loadAudioDevices()
    }

    val bgColor = remember(track?.id) {
        val hash = abs(track?.id?.hashCode() ?: 0)
        val r = (hash and 0xFF0000) shr 16
        val g = (hash and 0x00FF00) shr 8
        val b = hash and 0x0000FF
        Color(r, g, b).copy(alpha = 0.5f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(
                colors = listOf(bgColor, com.musically.studio.ui.theme.MaveBackground)
            ))
    ) {
        if (track == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            if (isExpanded) {
                TwoPaneNowPlaying(
                    track = track,
                    isPlaying = isPlaying,
                    trackProgress = trackProgress,
                    isShuffleEnabled = isShuffleEnabled,
                    isRepeatEnabled = isRepeatEnabled,
                    currentCoverUrl = currentCoverUrl,
                    currentVideoUrl = currentVideoUrl,
                    devices = devices,
                    modality = modality,
                    onCollapse = onCollapse,
                    onMore = { onMoreOptions(track.id) },
                    onSeek = { viewModel.seekTo(it) },
                    onTogglePlay = { viewModel.togglePlayPause() },
                    onNext = { viewModel.skipNext() },
                    onPrevious = { viewModel.skipPrevious() },
                    onToggleShuffle = { viewModel.toggleShuffle() },
                    onToggleRepeat = { viewModel.toggleRepeat() },
                    onLike = { viewModel.likeTrack(track.id) },
                    onBookmark = { viewModel.bookmarkTrack(track.id) },
                    onShare = { 
                        viewModel.shareTrack(track.id) { url ->
                            url?.let {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, it)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                localCtx.startActivity(shareIntent)
                            }
                        }
                    },
                    onQueueClick = onQueueClick,
                    onLyricsClick = onLyricsClick,
                    onDeviceClick = onDeviceClick,
                    onRequestCover = { viewModel.requestCoverArt() },
                    onRequestVideo = { viewModel.requestMusicVideo() },
                    volume = volume,
                    onVolumeChange = { viewModel.setVolume(it) }
                )
            } else {
                CompactNowPlaying(
                    track = track,
                    isPlaying = isPlaying,
                    trackProgress = trackProgress,
                    isShuffleEnabled = isShuffleEnabled,
                    isRepeatEnabled = isRepeatEnabled,
                    currentCoverUrl = currentCoverUrl,
                    currentVideoUrl = currentVideoUrl,
                    devices = devices,
                    modality = modality,
                    onCollapse = onCollapse,
                    onMore = { onMoreOptions(track.id) },
                    onSeek = { viewModel.seekTo(it) },
                    onTogglePlay = { viewModel.togglePlayPause() },
                    onNext = { viewModel.skipNext() },
                    onPrevious = { viewModel.skipPrevious() },
                    onToggleShuffle = { viewModel.toggleShuffle() },
                    onToggleRepeat = { viewModel.toggleRepeat() },
                    onLike = { viewModel.likeTrack(track.id) },
                    onBookmark = { viewModel.bookmarkTrack(track.id) },
                    onShare = { 
                        viewModel.shareTrack(track.id) { url ->
                            url?.let {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, it)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                localCtx.startActivity(shareIntent)
                            }
                        }
                    },
                    onQueueClick = onQueueClick,
                    onLyricsClick = onLyricsClick,
                    onDeviceClick = onDeviceClick,
                    onRequestCover = { viewModel.requestCoverArt() },
                    onRequestVideo = { viewModel.requestMusicVideo() },
                    volume = volume,
                    onVolumeChange = { viewModel.setVolume(it) }
                )
            }
        }
    }
}
