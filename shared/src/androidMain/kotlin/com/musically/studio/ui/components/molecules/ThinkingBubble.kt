/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: Android Component for ThinkingBubble.kt
 */

package com.musically.studio.ui.components.molecules

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedMarble(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "marble")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "marbleRotation"
    )

    val colors = listOf(
        com.musically.studio.ui.theme.MavePurple500,
        com.musically.studio.ui.theme.MaveCyan500,
        Color(0xFFFF007F), // Neon pink
        com.musically.studio.ui.theme.MavePurple500
    )

    Box(
        modifier = modifier
            .clip(CircleShape)
            .graphicsLayer { rotationZ = rotation }
            .background(Brush.sweepGradient(colors))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { rotationZ = -rotation * 1.5f }
                .background(Brush.radialGradient(colors.reversed(), radius = 50f), alpha = 0.5f)
        )
    }
}

@Composable
fun ThinkingBubble(text: String, modifier: Modifier = Modifier) {
    val alpha by rememberInfiniteTransition(label = "think").animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "thinkAlpha"
    )
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top) {
        
        AnimatedMarble(modifier = Modifier.size(32.dp))
        
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier.widthIn(max = 280.dp)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                .background(com.musically.studio.ui.theme.MaveDarkSurfaceVariant)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(text = text, style = MaterialTheme.typography.bodySmall,
                color = com.musically.studio.ui.theme.MaveBlueGray400.copy(alpha = alpha), maxLines = 4)
        }
    }
}
