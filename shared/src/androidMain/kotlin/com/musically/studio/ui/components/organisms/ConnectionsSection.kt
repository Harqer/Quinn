package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.*
import com.musically.studio.ui.components.atoms.ConnectionButton

@Composable
fun ConnectionsSection(
    viewModel: MainViewModel,
    spotifyConnected: Boolean,
    youtubeConnected: Boolean
) {
    val isCompact = !androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2().windowSizeClass.isWidthAtLeastBreakpoint(600)
    
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("Connections", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        if (isCompact) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ConnectionButton("Spotify", spotifyConnected) { viewModel.connectSpotify() }
                ConnectionButton("YouTube", youtubeConnected) { viewModel.connectYouTube() }
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ConnectionButton("Spotify", spotifyConnected, modifier = Modifier.weight(1f)) { viewModel.connectSpotify() }
                ConnectionButton("YouTube", youtubeConnected, modifier = Modifier.weight(1f)) { viewModel.connectYouTube() }
            }
        }
    }
}
