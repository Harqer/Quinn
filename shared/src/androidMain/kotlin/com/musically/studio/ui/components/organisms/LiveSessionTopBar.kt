/**
 * @AtomicLevel: Organism
 * @SemanticPurpose: Android Component for LiveSessionTopBar.kt
 */

package com.musically.studio.ui.components.organisms
import androidx.compose.material3.MaterialTheme

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.*

@Composable
fun LiveSessionTopBar(
    viewModel: MainViewModel,
    isLiveSessionActive: Boolean,
    isWearableConnected: Boolean,
    isWearableStreamingEnabled: Boolean,
    generatedPrompts: List<String>,
    onNavigateBack: () -> Unit,
    onMoreOptionsClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Top App Bar
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Mave Live", style = MaterialTheme.typography.titleMedium,
                    color = Color.White, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(8.dp).background(
                        if (isLiveSessionActive) com.musically.studio.ui.theme.MaveGreen500 else com.musically.studio.ui.theme.MaveGray400, CircleShape))
                    Text(
                        text = if (isLiveSessionActive) "Connected" else "Disconnected",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isLiveSessionActive) com.musically.studio.ui.theme.MaveGreen500 else com.musically.studio.ui.theme.MaveGray400
                    )
                }
            }
            Row {
                if (isWearableConnected) {
                    IconButton(onClick = { viewModel.toggleWearableFrameStreaming() }) {
                        Icon(
                            imageVector = if (isWearableStreamingEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                            contentDescription = "Toggle Wearable Camera Stream",
                            tint = if (isWearableStreamingEnabled) com.musically.studio.ui.theme.MaveGreen500 else com.musically.studio.ui.theme.MaveGray400
                        )
                    }
                }
                IconButton(onClick = onMoreOptionsClick) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
                }
            }
        }

        // Generated prompts banner
        AnimatedVisibility(
            visible = generatedPrompts.isNotEmpty(),
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = com.musically.studio.ui.theme.MaveDarkSurfaceVariant),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Generated Music Vibes", style = MaterialTheme.typography.labelMedium,
                        color = com.musically.studio.ui.theme.MaveBlueGray400, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    generatedPrompts.take(3).forEach { prompt ->
                        Row(modifier = Modifier.padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MusicNote, contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(prompt, style = MaterialTheme.typography.bodySmall,
                                color = Color.White, maxLines = 2)
                        }
                    }
                }
            }
        }
    }
}
