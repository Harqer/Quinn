/**
 * @AtomicLevel: Atom
 * @SemanticPurpose: Android Component for CameraGridOverlay.kt
 */

package com.musically.studio.ui.components.molecules

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp

@Composable
fun CameraGridOverlay(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val stroke = 1.dp.toPx()
        val gridColor = Color.White.copy(alpha = 0.35f)
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

        drawLine(gridColor, Offset(w / 3, 0f), Offset(w / 3, h), strokeWidth = stroke, pathEffect = pathEffect)
        drawLine(gridColor, Offset(2 * w / 3, 0f), Offset(2 * w / 3, h), strokeWidth = stroke, pathEffect = pathEffect)
        drawLine(gridColor, Offset(0f, h / 3), Offset(w, h / 3), strokeWidth = stroke, pathEffect = pathEffect)
        drawLine(gridColor, Offset(0f, 2 * h / 3), Offset(w, 2 * h / 3), strokeWidth = stroke, pathEffect = pathEffect)
    }
}
