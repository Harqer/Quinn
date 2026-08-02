package com.musically.studio.ui.components.molecules

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptySessionState(onStartSession: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.WifiOff, contentDescription = null,
            tint = com.musically.studio.ui.theme.MaveGray600, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text("Session not connected", style = MaterialTheme.typography.titleMedium,
            color = com.musically.studio.ui.theme.MaveBlueGray400, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Tap below to start your real-time music generation session",
            style = MaterialTheme.typography.bodyMedium, color = com.musically.studio.ui.theme.MaveGray600,
            textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onStartSession) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Start Session")
        }
    }
}
