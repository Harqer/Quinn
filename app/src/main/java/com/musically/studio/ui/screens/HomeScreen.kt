package com.musically.studio.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.components.ChatBubble
import com.musically.studio.ui.components.POVView
import com.musically.studio.ui.components.CameraPreview
import com.musically.studio.ui.models.ChatMessage
import com.musically.studio.ui.theme.MaveBrand
import com.musically.studio.ui.theme.MaveBackground
import com.musically.studio.ui.theme.MaveSurfaceContainer
import com.musically.studio.ui.theme.MaveTheme

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    isWearableConnected: Boolean = false,
    onNavigateToDevices: () -> Unit = {}
) {
    val spacing = MaveTheme.spacing
    var inputText by remember { mutableStateOf("") }
    var isLivePovEnabled by remember { mutableStateOf(false) }
    val thinkingText by viewModel.thinkingText.collectAsStateWithLifecycle()
    val isRecording by viewModel.isRecording.collectAsStateWithLifecycle()
    val isHapticEnabled by viewModel.isHapticFeedbackEnabled.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    Scaffold(
        containerColor = MaveBackground,
        bottomBar = {
            StudioChatInputBar(
                text = inputText,
                isLive = isLivePovEnabled,
                isRecording = isRecording,
                onTextChange = { inputText = it },
                onToggleLive = { 
                    if (isHapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    isLivePovEnabled = !isLivePovEnabled 
                },
                onSend = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendTextCommand(inputText)
                        inputText = ""
                    }
                },
                onRecordVoice = { 
                    if (isHapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.recordVoice() 
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Immersive POV Layer
            if (isLivePovEnabled) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                    if (isWearableConnected) {
                        POVView(modifier = Modifier.fillMaxSize().alpha(0.4f))
                    } else {
                        CameraPreview(
                            modifier = Modifier.fillMaxSize().alpha(0.4f),
                            viewModel = viewModel
                        )
                    }
                }
            }

            Column(modifier = Modifier.fillMaxSize()) {
                // Header: Mave Studio Hub
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = spacing.large, vertical = spacing.studioHeader),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mave Studio",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateToDevices) {
                            Icon(
                                imageVector = Icons.Default.DeviceUnknown,
                                contentDescription = "Connect Glasses",
                                tint = if (isWearableConnected) MaveBrand else Color.White
                            )
                        }
                    }
                }

                if (viewModel.messages.isEmpty() && thinkingText.isBlank()) {
                    MaveWelcomeState(onVibeSelected = { viewModel.sendTextCommand(it) })
                } else {
                    // Chat History
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentPadding = PaddingValues(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        reverseLayout = true
                    ) {
                        if (thinkingText.isNotBlank()) {
                            item {
                                ThinkingBubble(text = thinkingText)
                            }
                        }

                        items(viewModel.messages) { message: ChatMessage ->
                            ChatBubble(
                                message = message,
                                onLike = { message.trackId?.let { viewModel.saveTrackToLibrary(it) } },
                                onBookmark = { message.trackId?.let { viewModel.bookmarkTrack(it) } }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MaveWelcomeState(onVibeSelected: (String) -> Unit) {
    val suggestedActions = listOf(
        "Generate Music", "Start Podcast", "Narrate Audiobook", "Compose a vibe", "Tell a story", "Soundtrack this moment"
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Millions of Vibes.\nOrchestrated by Mave.",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(suggestedActions) { action ->
                VibeCard(label = action, onClick = { onVibeSelected(action) })
            }
        }
    }
}

@Composable
private fun VibeCard(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = MaveSurfaceContainer,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.height(100.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaveBrand,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ThinkingBubble(text: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Surface(
            color = MaveSurfaceContainer.copy(alpha = 0.6f),
            shape = MaterialTheme.shapes.medium,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaveBrand.copy(alpha = 0.2f)),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaveBrand,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

@Composable
private fun StudioChatInputBar(
    text: String,
    isLive: Boolean,
    isRecording: Boolean,
    onTextChange: (String) -> Unit,
    onToggleLive: () -> Unit,
    onSend: () -> Unit,
    onRecordVoice: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Surface(
        color = MaveSurfaceContainer,
        tonalElevation = 12.dp,
        modifier = Modifier.imePadding()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .navigationBarsPadding()
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggleLive,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = if (isLive) MaveBrand.copy(alpha = pulseAlpha) else Color.Transparent
                )
            ) {
                Icon(
                    Icons.Default.CameraAlt,
                    tint = if (isLive) MaveBrand else Color.White,
                    contentDescription = "POV"
                )
            }
            IconButton(
                onClick = onRecordVoice,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = if (isRecording) MaveBrand.copy(alpha = pulseAlpha) else Color.Transparent
                )
            ) {
                Icon(
                    Icons.Default.Mic,
                    tint = if (isRecording) MaveBrand else Color.White,
                    contentDescription = "Voice"
                )
            }
            TextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { 
                    Text(
                        "Generate...", 
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    ) 
                },
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            IconButton(
                onClick = onSend,
                enabled = text.isNotBlank(),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (text.isNotBlank()) Color.White else Color.DarkGray,
                    contentColor = Color.Black
                )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    modifier = Modifier.size(20.dp),
                    contentDescription = "Send"
                )
            }
        }
    }
}
