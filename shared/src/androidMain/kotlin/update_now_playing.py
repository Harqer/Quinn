import os
import re

file_path = "/home/shaolin/lyria/shared/src/androidMain/kotlin/com/musically/studio/ui/screens/NowPlayingScreen.kt"

with open(file_path, "r") as f:
    content = f.read()

# Add onDeviceClick to NowPlayingScreen signature
content = content.replace(
"""    onQueueClick: () -> Unit = {},
    onLyricsClick: () -> Unit = {}
) {""", 
"""    onQueueClick: () -> Unit = {},
    onLyricsClick: () -> Unit = {},
    onDeviceClick: () -> Unit = {}
) {""")

# Add volume to NowPlayingScreen
content = content.replace(
"""    val devices by viewModel.devices.collectAsStateWithLifecycle()
    val localCtx = androidx.compose.ui.platform.LocalContext.current""",
"""    val devices by viewModel.devices.collectAsStateWithLifecycle()
    val volume by viewModel.volume.collectAsStateWithLifecycle()
    val localCtx = androidx.compose.ui.platform.LocalContext.current""")

# Add volume and onVolumeChange to TwoPaneNowPlaying call
content = content.replace(
"""                    onQueueClick = onQueueClick,
                    onLyricsClick = onLyricsClick,
                    onRequestCover = { viewModel.requestCoverArt() },
                    onRequestVideo = { viewModel.requestMusicVideo() }
                )""",
"""                    onQueueClick = onQueueClick,
                    onLyricsClick = onLyricsClick,
                    onDeviceClick = onDeviceClick,
                    onRequestCover = { viewModel.requestCoverArt() },
                    onRequestVideo = { viewModel.requestMusicVideo() },
                    volume = volume,
                    onVolumeChange = { viewModel.setVolume(it) }
                )""")

# Update signatures of TwoPaneNowPlaying and CompactNowPlaying
content = content.replace(
"""    onQueueClick: () -> Unit,
    onLyricsClick: () -> Unit,
    onRequestCover: () -> Unit,
    onRequestVideo: () -> Unit
) {""",
"""    onQueueClick: () -> Unit,
    onLyricsClick: () -> Unit,
    onDeviceClick: () -> Unit,
    onRequestCover: () -> Unit,
    onRequestVideo: () -> Unit,
    volume: Float,
    onVolumeChange: (Float) -> Unit
) {""")

# Update device row in TwoPaneNowPlaying
content = content.replace(
"""            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Bluetooth, contentDescription = "Device", tint = com.musically.studio.ui.theme.MaveBrand, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(devices.firstOrNull()?.name ?: "Phone Speaker", color = com.musically.studio.ui.theme.MaveBrand, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }""",
"""            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onDeviceClick() }
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.Bluetooth, contentDescription = "Device", tint = com.musically.studio.ui.theme.MaveBrand, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(devices.firstOrNull()?.name ?: "Phone Speaker", color = com.musically.studio.ui.theme.MaveBrand, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }""")

# Add VolumeSlider Below PlaybackControls
content = content.replace(
"""        PlaybackControls(
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
        
        Row(""",
"""        PlaybackControls(
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
        
        Row(""")


# Add VolumeSlider composable at the end
content += """
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
"""

with open(file_path, "w") as f:
    f.write(content)
print("Updated NowPlayingScreen.kt")
