package com.musically.studio.ui.components.molecules

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun CameraFocusRing(
    tapPoint: Offset,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawCircle(
            color = Color.Yellow.copy(alpha = 0.85f),
            radius = 36.dp.toPx(),
            center = tapPoint,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}
