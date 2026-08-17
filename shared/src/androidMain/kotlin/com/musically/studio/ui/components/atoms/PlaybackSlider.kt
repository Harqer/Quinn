/**
 * @AtomicLevel: Atom
 * @SemanticPurpose: Android Component for PlaybackSlider.kt
 */

package com.musically.studio.ui.components.atoms

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import java.util.*

@Composable
fun PlaybackSlider(
    progress: Float,
    durationMs: Long,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderPosition by remember(progress) { mutableFloatStateOf(progress) }

    Column(modifier = modifier.fillMaxWidth()) {
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
}

fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%d:%02d", minutes, seconds)
}
