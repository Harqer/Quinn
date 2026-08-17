/**
 * @AtomicLevel: Atom
 * @SemanticPurpose: Android Component for VolumeSlider.kt
 */

package com.musically.studio.ui.components.atoms

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VolumeSlider(
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(Icons.AutoMirrored.Filled.VolumeDown, contentDescription = "Volume Down", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
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
        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Volume Up", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
    }
}
