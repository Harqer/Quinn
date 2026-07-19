package com.musically.studio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.components.ChatBubble
import com.musically.studio.ui.components.POVView
import com.musically.studio.ui.components.CameraPreview
import com.musically.studio.ui.models.ChatMessage

@Composable
fun PodcastScreen(
    viewModel: MainViewModel,
    isWearableConnected: Boolean = false
) {
    var isLiveEnabled by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            PodcastInputBar(
                text = inputText,
                onTextChange = { inputText = it },
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
            Text(
                text = "Quinn's Podcast",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            if (isLiveEnabled) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .height(200.dp)
                        .fillMaxWidth()
                        .background(Color.Black, MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    if (isWearableConnected) {
                        POVView(modifier = Modifier.fillMaxSize())
                    } else {
                        CameraPreview(modifier = Modifier.fillMaxSize())
                    }
                }
            } else {
                Button(
                    onClick = { 
                        isLiveEnabled = true
                        viewModel.switchMode("podcast")
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
                ) {
                    Text("Start Live Podcast Session")
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(viewModel.podcastMessages) { message: ChatMessage ->
                    Column {
                        ChatBubble(message)
                        if (!message.isUser) {
                            Row(
                                modifier = Modifier.padding(top = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TextButton(onClick = { /* Share */ }) {
                                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Share")
                                }
                                TextButton(onClick = { viewModel.savePodcastToSpotify("spotify:track:placeholder") }) {
                                    Icon(Icons.Default.LibraryMusic, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Save to Library")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PodcastInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onRecordVoice: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .navigationBarsPadding()
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onRecordVoice) {
                Icon(Icons.Default.Mic, contentDescription = "Voice", tint = MaterialTheme.colorScheme.primary)
            }
            TextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text("Tell Quinn what to talk about...") },
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
            IconButton(onClick = onSend, enabled = text.isNotBlank()) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
