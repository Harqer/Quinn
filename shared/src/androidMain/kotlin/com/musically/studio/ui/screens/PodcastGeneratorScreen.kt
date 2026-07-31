package com.musically.studio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.filled.Stop
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.ui.MainViewModel
import androidx.compose.animation.core.*
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.models.ChatMessage
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import com.musically.studio.ui.theme.MaveStyles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PodcastGeneratorScreen(
    viewModel: MainViewModel,
    onNavigateToSettings: () -> Unit = {}
) {
    var promptText by remember { mutableStateOf("") }
    
    // Lumina Synth Design System Colors
    val surfaceColor = com.musically.studio.ui.theme.LocalMaveColorScheme.current.surfaceContainerHigh
    val backgroundColor = com.musically.studio.ui.theme.LocalMaveColorScheme.current.background
    val primaryElectricViolet = com.musically.studio.ui.theme.MavePurple500
    val secondaryNeonCyan = com.musically.studio.ui.theme.MaveCyan500
    val onSurfaceColor = com.musically.studio.ui.theme.LocalMaveColorScheme.current.onSurface
    
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Mave Studio", 
                        color = onSurfaceColor,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = onSurfaceColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(surfaceColor.copy(alpha = 0.8f))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = promptText,
                        onValueChange = { promptText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Describe the podcast you want to create...", color = Color.Gray) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = onSurfaceColor,
                            unfocusedTextColor = onSurfaceColor
                        )
                    )
                    
                    val isRecording by viewModel.isRecording.collectAsStateWithLifecycle()
                    val context = LocalContext.current
                    
                    IconButton(
                        onClick = { viewModel.recordVoice(context) },
                        modifier = Modifier
                            .background(
                                brush = Brush.linearGradient(listOf(primaryElectricViolet, secondaryNeonCyan)),
                                shape = RoundedCornerShape(50)
                            )
                    ) {
                        Icon(
                            if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            tint = Color.White
                        )
                    }
                    
                    if (promptText.isNotBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                viewModel.generatePodcast(promptText)
                                promptText = ""
                            },
                            modifier = Modifier
                                .background(surfaceColor, RoundedCornerShape(50))
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = secondaryNeonCyan)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(max = 840.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp
            ),
            reverseLayout = true
        ) {
            val messages = viewModel.messages
            items(messages) { msg ->
                if (msg.isUser) {
                    UserMessageBubble(msg.text)
                } else {
                    AIMessageBubble(
                        msg = msg,
                        viewModel = viewModel,
                        primaryColor = primaryElectricViolet,
                        secondaryColor = secondaryNeonCyan
                    )
                }
            }
        }
    }
}
}

@Composable
fun UserMessageBubble(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource) { it.isEnabled = true }
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Box(
            modifier = Modifier
                .styleable(styleState, MaveStyles.userMessageBubbleStyle, style)
        ) {
            Text(text, color = com.musically.studio.ui.theme.MaveGray200)
        }
    }
}

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
                            .clip(RoundedCornerShape(8.dp))
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
                            .clip(RoundedCornerShape(8.dp))
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
