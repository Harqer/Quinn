package com.musically.studio.ui.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import android.view.HapticFeedbackConstants
import androidx.compose.ui.platform.LocalView
import com.musically.studio.ui.utils.debouncedClickable

@Composable
fun PlaybackControls(
    isPlaying: Boolean,
    isShuffleEnabled: Boolean,
    isRepeatEnabled: String,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onPlayPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
            onToggleShuffle()
        }) {
            Icon(Icons.Default.Shuffle, contentDescription = "Shuffle", tint = if (isShuffleEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground)
        }
        IconButton(onClick = {
            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
            onPrevious()
        }) {
            Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(36.dp))
        }
        
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .debouncedClickable {
                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                    onPlayPause()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "Play/Pause",
                tint = Color.Black,
                modifier = Modifier.size(36.dp)
            )
        }
        
        IconButton(onClick = {
            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
            onNext()
        }) {
            Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(36.dp))
        }
        IconButton(onClick = {
            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
            onToggleRepeat()
        }) {
            Icon(
                if (isRepeatEnabled == "one") Icons.Default.RepeatOne else Icons.Default.Repeat,
                contentDescription = "Repeat",
                tint = if (isRepeatEnabled != "none") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
