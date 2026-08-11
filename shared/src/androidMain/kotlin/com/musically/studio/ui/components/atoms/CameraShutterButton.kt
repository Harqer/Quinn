package com.musically.studio.ui.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.screens.CameraCaptureMode

@Composable
fun CameraShutterButton(
    captureMode: CameraCaptureMode,
    isCapturing: Boolean,
    isRecordingVideo: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val description = if (captureMode == CameraCaptureMode.VIDEO) {
        if (isRecordingVideo) "Stop Recording Video" else "Start Recording Video"
    } else {
        "Take Photo"
    }

    IconButton(
        onClick = onClick,
        enabled = !isCapturing,
        modifier = modifier
            .size(80.dp)
            .border(4.dp, if (captureMode == CameraCaptureMode.VIDEO) Color.Red else Color.White, CircleShape)
            .background(Color.Transparent, CircleShape)
            .semantics(mergeDescendants = true) {
                contentDescription = description
            }
    ) {
        Box(
            modifier = Modifier
                .size(if (isRecordingVideo) 36.dp else 64.dp)
                .background(
                    if (captureMode == CameraCaptureMode.VIDEO) Color.Red else (if (isCapturing) Color.LightGray else Color.White),
                    if (isRecordingVideo) RoundedCornerShape(8.dp) else CircleShape
                )
        )
    }
}
