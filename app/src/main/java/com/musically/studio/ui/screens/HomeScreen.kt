package com.musically.studio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.components.ChatBubble
import com.musically.studio.ui.components.POVView
import com.musically.studio.ui.components.CameraPreview
import com.musically.studio.ui.models.ChatMessage

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    isWearableConnected: Boolean = false
) {
    var inputText by remember { mutableStateOf("") }
    var isLivePovEnabled by remember { mutableStateOf(false) }
    val currentMode by viewModel.currentMode.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            HomeChatInputBar(
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Immersive POV Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                if (isLivePovEnabled) {
                    if (isWearableConnected) {
                        POVView(modifier = Modifier.fillMaxSize())
                    } else {
                        CameraPreview(modifier = Modifier.fillMaxSize())
                    }
                    
                    // Mode Selector in POV
                    Surface(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
                    ) {
                        Row(modifier = Modifier.padding(4.dp)) {
                            IconButton(
                                onClick = { viewModel.switchMode("music") },
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = if (currentMode == "music") MaterialTheme.colorScheme.primary else Color.White
                                )
                            ) {
                                Icon(Icons.Default.MusicNote, contentDescription = "Music Mode")
                            }
                            IconButton(
                                onClick = { viewModel.switchMode("podcast") },
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = if (currentMode == "podcast") MaterialTheme.colorScheme.primary else Color.White
                                )
                            ) {
                                Icon(Icons.Default.Podcasts, contentDescription = "Podcast Mode")
                            }
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                    ) {
                        Text(
                            text = "LIVE POV",
                            color = MaterialTheme.colorScheme.onError,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Enable POV to start creating",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Chat History / Director Log
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.messages) { message: ChatMessage ->
                    ChatBubble(
                        message = message,
                        onSave = { message.trackId?.let { viewModel.saveTrackToLibrary(it) } }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeChatInputBar(
    text: String,
    isLive: Boolean,
    onTextChange: (String) -> Unit,
    onToggleLive: () -> Unit,
    onSend: () -> Unit,
    onRecordVoice: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 3.dp
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
                    containerColor = if (isLive) MaterialTheme.colorScheme.errorContainer else Color.Transparent
                )
            ) {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = "Toggle POV",
                    tint = if (isLive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onRecordVoice) {
                Icon(Icons.Default.Mic, contentDescription = "Voice", tint = MaterialTheme.colorScheme.secondary)
            }
            TextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text("Tell Quinn your vibe...") },
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            IconButton(
                onClick = onSend,
                enabled = text.isNotBlank(),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}
