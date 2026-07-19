package com.musically.studio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.components.POVView
import com.musically.studio.ui.components.CameraPreview
import com.musically.studio.ui.models.ChatMessage
import com.musically.studio.ui.theme.SpotifyBlack
import com.musically.studio.ui.theme.SpotifyDarkGray
import com.musically.studio.ui.theme.SpotifyGreen

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    isWearableConnected: Boolean = false,
    onNavigateToDevices: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var isLivePovEnabled by remember { mutableStateOf(false) }
    val currentMode by viewModel.currentMode.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = SpotifyBlack,
        bottomBar = {
            StudioChatInputBar(
                text = inputText,
                isLive = isLivePovEnabled,
                onTextChange = { inputText = it },
                onToggleLive = { isLivePovEnabled = !isLivePovEnabled },
                onSend = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendTextCommand(inputText)
                        inputText = ""
                    }
                },
                onRecordVoice = { viewModel.recordVoice() }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Immersive POV Layer (Subtle background when enabled)
            if (isLivePovEnabled) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                    if (isWearableConnected) {
                        POVView(modifier = Modifier.fillMaxSize().alpha(0.4f))
                    } else {
                        CameraPreview(modifier = Modifier.fillMaxSize().alpha(0.4f))
                    }
                }
            }

            Column(modifier = Modifier.fillMaxSize()) {
                // Top Hub: Mode Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Studio",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = SpotifyDarkGray.copy(alpha = 0.8f),
                            shape = MaterialTheme.shapes.extraLarge
                        ) {
                            Row(modifier = Modifier.padding(4.dp)) {
                                ModeTab(
                                    selected = currentMode == "music",
                                    icon = Icons.Default.MusicNote,
                                    onClick = { viewModel.switchMode("music") }
                                )
                                ModeTab(
                                    selected = currentMode == "podcast",
                                    icon = Icons.Default.Podcasts,
                                    onClick = { viewModel.switchMode("podcast") }
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        IconButton(onClick = onNavigateToDevices) {
                            Icon(Icons.Default.Devices, contentDescription = "Devices", tint = Color.White)
                        }
                    }
                }

                // Chat History
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(viewModel.messages) { message: ChatMessage ->
                        ConversationalBubble(
                            message = message,
                            onLike = { viewModel.saveTrackToLibrary(message.trackId ?: "") },
                            onBookmark = { viewModel.bookmarkTrack(message.trackId ?: "") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeTab(
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = if (selected) SpotifyGreen else Color.Gray
        )
    ) {
        Icon(icon, contentDescription = null)
    }
}

@Composable
fun ConversationalBubble(
    message: ChatMessage,
    onLike: () -> Unit,
    onBookmark: () -> Unit
) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val containerColor = if (message.isUser) Color(0xFF2E2E2E) else SpotifyGreen
    val contentColor = if (message.isUser) Color.White else Color.Black

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = containerColor,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = contentColor
                )
                
                if (!message.isUser && message.trackId != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = contentColor.copy(alpha = 0.2f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = onLike) {
                            Icon(Icons.Default.FavoriteBorder, contentDescription = "Like", tint = contentColor, modifier = Modifier.size(20.dp))
                        }
                        IconButton(onClick = onBookmark) {
                            Icon(Icons.Default.BookmarkBorder, contentDescription = "Bookmark", tint = contentColor, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
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
        color = SpotifyDarkGray,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .navigationBarsPadding()
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggleLive) {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = "POV",
                    tint = if (isLive) SpotifyGreen else Color.White
                )
            }
            IconButton(onClick = onRecordVoice) {
                Icon(Icons.Default.Mic, contentDescription = "Voice", tint = Color.White)
            }
            TextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text("Ask Quinn your vibe...") },
                modifier = Modifier.weight(1f),
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
                enabled = text.isNotBlank()
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (text.isNotBlank()) SpotifyGreen else Color.Gray
                )
            }
        }
    }
}
