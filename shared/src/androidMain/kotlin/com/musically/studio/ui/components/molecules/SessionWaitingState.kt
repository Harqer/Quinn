package com.musically.studio.ui.components.molecules

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SessionWaitingState() {
    val alpha by rememberInfiniteTransition(label = "wait").animateFloat(
        initialValue = 0.3f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "waitAlpha"
    )
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.GraphicEq, contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = alpha),
            modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text("Connected — say something or type a song description",
            style = MaterialTheme.typography.bodyLarge, color = com.musically.studio.ui.theme.MaveBlueGray400,
            textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text("You can also tap the camera icon to generate music from a photo",
            style = MaterialTheme.typography.bodySmall, color = com.musically.studio.ui.theme.MaveGray600,
            textAlign = TextAlign.Center)
    }
}
