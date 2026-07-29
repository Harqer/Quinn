package com.musically.studio.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.musically.studio.shared.R
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.MainViewModel
import java.util.*
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

    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isExpanded = adaptiveInfo.windowSizeClass.windowWidthDp >= 840



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

@Composable
private fun TwoPaneNowPlaying(
    track: MaveTrack,
    isPlaying: Boolean,
    trackProgress: Float,
    isShuffleEnabled: Boolean,
    isRepeatEnabled: String,
    currentCoverUrl: String?,
    currentVideoUrl: String?,
    devices: List<com.musically.studio.ui.models.AudioDevice>,
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
    val localCtx = androidx.compose.ui.platform.LocalContext.current
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

@Composable
private fun CompactNowPlaying(
    track: MaveTrack,
    isPlaying: Boolean,
    trackProgress: Float,
    isShuffleEnabled: Boolean,
    isRepeatEnabled: String,
    currentCoverUrl: String?,
    currentVideoUrl: String?,
    devices: List<com.musically.studio.ui.models.AudioDevice>,
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
    val localCtx = androidx.compose.ui.platform.LocalContext.current
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
        
        Spacer(modifier = Modifier.weight(1f))
        
        val lyricsColor = remember(track.id) {
            val hash = kotlin.math.abs(track.id.hashCode() * 31)
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

@Composable
private fun PlaybackSlider(
    progress: Float,
    durationMs: Long,
    onSeek: (Float) -> Unit
) {
    var sliderPosition by remember(progress) { mutableFloatStateOf(progress) }

    Slider(
        value = sliderPosition,
        onValueChange = { sliderPosition = it },
        onValueChangeFinished = { onSeek(sliderPosition) },
        colors = SliderDefaults.colors(
            thumbColor = Color.White,
            activeTrackColor = Color.White,
            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth()
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(formatDuration((sliderPosition * durationMs).toLong()), style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
        Text(formatDuration(durationMs), style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
    }
}

@Composable
private fun PlaybackControls(
    isPlaying: Boolean,
    isShuffleEnabled: Boolean,
    isRepeatEnabled: String,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onPlayPause: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onToggleShuffle) {
            Icon(Icons.Default.Shuffle, contentDescription = "Shuffle", tint = if (isShuffleEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground)
        }
        IconButton(onClick = onPrevious) {
            Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(36.dp))
        }
        
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(Color.White, CircleShape)
                .clickable { onPlayPause() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "Play/Pause",
                tint = Color.Black,
                modifier = Modifier.size(36.dp)
            )
        }
        
        IconButton(onClick = onNext) {
            Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(36.dp))
        }
        IconButton(onClick = onToggleRepeat) {
            Icon(
                if (isRepeatEnabled == "one") Icons.Default.RepeatOne else Icons.Default.Repeat,
                contentDescription = "Repeat",
                tint = if (isRepeatEnabled != "none") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
private fun SeamlessVideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = androidx.media3.common.MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            repeatMode = Player.REPEAT_MODE_ALL
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }
        },
        modifier = modifier
    )
}

private fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%d:%02d", minutes, seconds)
}

@Composable
private fun VolumeSlider(
    volume: Float,
    onVolumeChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(Icons.Default.VolumeDown, contentDescription = "Volume Down", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
        Slider(
            value = volume,
            onValueChange = onVolumeChange,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
            ),
            modifier = Modifier.weight(1f).height(24.dp)
        )
        Icon(Icons.Default.VolumeUp, contentDescription = "Volume Up", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
    }
}
