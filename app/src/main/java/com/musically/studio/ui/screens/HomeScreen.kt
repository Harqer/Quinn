package com.musically.studio.ui.screens

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

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    isWearableConnected: Boolean = false,
    onNavigateToDevices: () -> Unit = {}
) {
    var inputText by remember { mutableStateOf("") }
    var isLivePovEnabled by remember { mutableStateOf(false) }
    val currentMode by viewModel.currentMode.collectAsStateWithLifecycle()
    val thinkingText by viewModel.thinkingText.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    Scaffold(
        containerColor = MaveBackground,
        bottomBar = {
            StudioChatInputBar(
                text = inputText,
                isLive = isLivePovEnabled,
                onTextChange = { inputText = it },
                onToggleLive = { 
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    isLivePovEnabled = !isLivePovEnabled 
                },
                onSend = {
                    if (inputText.isNotBlank()) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.sendTextCommand(inputText)
                        inputText = ""
                    }
                },
                onRecordVoice = { 
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
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
                        .padding(horizontal = 24.dp, vertical = 20.dp),
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
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MaveSurfaceContainer.copy(alpha = 0.8f),
                            shape = MaterialTheme.shapes.extraLarge,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                        ) {
                            Row(modifier = Modifier.padding(4.dp)) {
                                ModeIcon(
                                    selected = currentMode == "music",
                                    icon = Icons.Default.MusicNote,
                                    onClick = { 
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        viewModel.switchMode("music") 
                                    }
                                )
                                ModeIcon(
                                    selected = currentMode == "podcast",
                                    icon = Icons.Default.Podcasts,
                                    onClick = { 
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        viewModel.switchMode("podcast") 
                                    }
                                )
                            }
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
    val quickVibes = listOf(
        "Neo-Soul Jam", "Cinematic POV", "Techno Pulse", "Ambient Drift", "Hip-Hop Flow", "Jazz Warp"
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "What's the vibe today?",
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
            items(quickVibes) { vibe ->
                VibeCard(label = vibe, onClick = { onVibeSelected(vibe) })
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        color = MaveBrand,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Mave Thinking",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaveBrand,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

@Composable
private fun ModeIcon(
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = if (selected) MaveBrand else Color.Gray
        )
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun StudioChatInputBar(
    text: String,
    isLive: Boolean,
    onTextChange: (String) -> Unit,
    onToggleLive: () -> Unit,
    onSend: () -> Unit,
    onRecordVoice: () -> Unit
) {
    Surface(
        color = MaveSurfaceContainer,
        tonalElevation = 12.dp
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
                    containerColor = if (isLive) Color.Red.copy(alpha = 0.1f) else Color.Transparent
                )
            ) {
                Icon(
                    Icons.Default.CameraAlt,
                    tint = if (isLive) Color.Red else Color.White,
                    contentDescription = "POV"
                )
            }
            IconButton(onClick = onRecordVoice) {
                Icon(Icons.Default.Mic, tint = Color.White, contentDescription = "Voice")
            }
            TextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { 
                    Text(
                        "Tell Mave your vibe...", 
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
