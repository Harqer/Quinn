/**
 * @AtomicLevel: Atom
 * @SemanticPurpose: Android Component for CameraExtensionBar.kt
 */

package com.musically.studio.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musically.studio.ui.screens.ActiveExtensionMode
import com.musically.studio.ui.utils.debouncedClickable

@Composable
fun CameraExtensionBar(
    selectedExtensionMode: ActiveExtensionMode,
    onExtensionSelected: (ActiveExtensionMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ActiveExtensionMode.values().forEach { mode ->
            val isSelected = selectedExtensionMode == mode
            Text(
                text = mode.label,
                color = if (isSelected) Color.Yellow else Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .semantics { contentDescription = "Camera Extension Mode ${mode.label}" }
                    .debouncedClickable { onExtensionSelected(mode) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
