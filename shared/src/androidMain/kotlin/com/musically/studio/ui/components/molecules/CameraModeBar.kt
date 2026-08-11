package com.musically.studio.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musically.studio.ui.screens.CameraCaptureMode
import com.musically.studio.ui.utils.debouncedClickable

@Composable
fun CameraModeBar(
    currentMode: CameraCaptureMode,
    onModeSelected: (CameraCaptureMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "PHOTO",
            color = if (currentMode == CameraCaptureMode.PHOTO) Color.Yellow else Color.White.copy(alpha = 0.6f),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier
                .semantics { contentDescription = "Photo Mode" }
                .debouncedClickable { onModeSelected(CameraCaptureMode.PHOTO) }
        )
        Text(
            text = "VIDEO",
            color = if (currentMode == CameraCaptureMode.VIDEO) Color.Red else Color.White.copy(alpha = 0.6f),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier
                .semantics { contentDescription = "Video Mode" }
                .debouncedClickable { onModeSelected(CameraCaptureMode.VIDEO) }
        )
    }
}
