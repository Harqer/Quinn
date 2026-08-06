package com.musically.studio.ui.screens
import androidx.compose.material3.MaterialTheme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedToggleIcon(enabled: Boolean, modifier: Modifier = Modifier) {
    val thumbX by animateFloatAsState(
        targetValue = if (enabled) 17f else 7f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "toggleThumb"
    )
    val iconColor = MaterialTheme.colorScheme.onSurfaceVariant

    Canvas(modifier = modifier.size(24.dp)) {
        val scaleX = size.width / 24f
        val scaleY = size.height / 24f

        scale(scaleX, scaleY, pivot = Offset.Zero) {
            val trackPath = Path().apply {
                fillType = PathFillType.NonZero
                // Outer track
                moveTo(7f, 18f)
                quadraticTo(4.5f, 18f, 2.75f, 16.25f)
                quadraticTo(1f, 14.5f, 1f, 12f)
                quadraticTo(1f, 9.5f, 2.75f, 7.75f)
                quadraticTo(4.5f, 6f, 7f, 6f)
                lineTo(17f, 6f)
                quadraticTo(19.5f, 6f, 21.25f, 7.75f)
                quadraticTo(23f, 9.5f, 23f, 12f)
                quadraticTo(23f, 14.5f, 21.25f, 16.25f)
                quadraticTo(19.5f, 18f, 17f, 18f)
                lineTo(7f, 18f)
                close()
                // Inner track (hole)
                moveTo(7f, 16f)
                lineTo(17f, 16f)
                quadraticTo(18.65f, 16f, 19.83f, 14.82f)
                quadraticTo(21f, 13.65f, 21f, 12f)
                quadraticTo(21f, 10.35f, 19.83f, 9.17f)
                quadraticTo(18.65f, 8f, 17f, 8f)
                lineTo(7f, 8f)
                quadraticTo(5.35f, 8f, 4.18f, 9.17f)
                quadraticTo(3f, 10.35f, 3f, 12f)
                quadraticTo(3f, 13.65f, 4.18f, 14.82f)
                quadraticTo(5.35f, 16f, 7f, 16f)
                close()
            }

            drawPath(
                path = trackPath,
                color = iconColor
            )

            drawCircle(
                color = iconColor,
                radius = 3f,
                center = Offset(thumbX, 12f)
            )
        }
    }
}
