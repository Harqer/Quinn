/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: Android Component for CameraZoomBar.kt
 */

package com.musically.studio.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musically.studio.ui.utils.debouncedClickable

@Composable
fun CameraZoomBar(
    currentLinearZoom: Float,
    onZoomSelected: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val presets = listOf(0f to "0.5x", 0.25f to "1x", 0.5f to "2x", 1.0f to "5x")
    Row(
        modifier = modifier.semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        presets.forEach { (ratio, label) ->
            val isSelected = (currentLinearZoom - ratio).let { it * it } < 0.01f
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.White else Color.Black.copy(alpha = 0.5f))
                    .semantics { contentDescription = "Zoom level $label" }
                    .debouncedClickable { onZoomSelected(ratio) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color.Black else Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
