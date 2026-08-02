package com.musically.studio.ui.components.molecules

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

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
        Box(
            modifier = Modifier.size(32.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = alpha),
                modifier = Modifier.size(16.dp))
        }
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
