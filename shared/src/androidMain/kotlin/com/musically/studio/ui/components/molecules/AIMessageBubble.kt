package com.musically.studio.ui.components.molecules

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.*
import com.musically.studio.ui.models.ChatMessage
import com.musically.studio.ui.theme.MaveStyles

@Composable
fun AIMessageBubble(
    msg: ChatMessage,
    viewModel: MainViewModel,
    primaryColor: Color,
    secondaryColor: Color,
    modifier: Modifier = Modifier,
    style: Style = Style
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource) { it.isEnabled = true }
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(listOf(primaryColor, secondaryColor)),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
                )
                .styleable(styleState, MaveStyles.aiMessageBubbleStyle, style)
        ) {
            Text(msg.text, color = com.musically.studio.ui.theme.MaveGray200)
            
            if (msg.trackId != null) {
                var realTrack by remember(msg.trackId) { mutableStateOf<MaveTrack?>(null) }
                LaunchedEffect(msg.trackId) {
                    realTrack = viewModel.getTrack(msg.trackId)
                }
                
                if (realTrack == null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small)
                            .background(com.musically.studio.ui.theme.MaveSurfaceVariant2)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = secondaryColor,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Generating track...", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    val displayTrack = realTrack!!
                    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
                    val currentTrack by viewModel.currentPlayingTrack.collectAsStateWithLifecycle()
                    val isThisTrackPlaying = isPlaying && currentTrack?.id == msg.trackId
    
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small)
                            .background(com.musically.studio.ui.theme.MaveSurfaceVariant2)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.playTrack(displayTrack) }) {
                            Icon(
                                if (isThisTrackPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = secondaryColor
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")
                        Row(
                            modifier = Modifier.weight(1f).height(24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            repeat(20) { index ->
                                val height by infiniteTransition.animateFloat(
                                    initialValue = 4f,
                                    targetValue = if (isThisTrackPlaying) ((index % 5) * 4 + 8).toFloat() else 4f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(durationMillis = 300 + (index * 50), easing = FastOutSlowInEasing),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "waveform_$index"
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(height.dp)
                                        .background(if (index < 8) primaryColor else Color.Gray, RoundedCornerShape(50))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
