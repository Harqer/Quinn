package com.musically.studio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.xr.glimmer.*
import com.musically.studio.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WearableActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            val viewModel: MainViewModel = viewModel()
            WearableContent(viewModel)
        }
    }
}

@Composable
fun WearableContent(viewModel: MainViewModel) {
    val thinkingText by viewModel.thinkingText.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    
    GlimmerTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black), // Additive display requirement
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // AI Reasoning HUD
                if (thinkingText.isNotEmpty()) {
                    Text(
                        text = thinkingText,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                // Intent Controls
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlimmerButton(
                        icon = Icons.Default.Mic,
                        onClick = { viewModel.recordVoice() }
                    )
                    GlimmerButton(
                        icon = Icons.Default.MusicNote,
                        onClick = { viewModel.sendTextCommand("Generate a new vibe") }
                    )
                }

                // Transport Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlimmerButton(
                        icon = Icons.Default.SkipPrevious,
                        onClick = { viewModel.skipPrevious() }
                    )
                    GlimmerButton(
                        icon = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        onClick = { viewModel.togglePlayPause() },
                        isPrimary = true
                    )
                    GlimmerButton(
                        icon = Icons.Default.SkipNext,
                        onClick = { viewModel.skipNext() }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun GlimmerButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    isPrimary: Boolean = false
) {
    androidx.xr.glimmer.Button(
        onClick = onClick,
        color = if (isPrimary) GlimmerTheme.colors.primary else GlimmerTheme.colors.surface,
    ) {
        androidx.xr.glimmer.Icon(
            imageVector = icon,
            contentDescription = null
        )
    }
}
