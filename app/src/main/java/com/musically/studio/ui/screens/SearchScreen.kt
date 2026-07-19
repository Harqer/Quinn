package com.musically.studio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.components.ChatBubble
import com.musically.studio.ui.components.POVView
import com.musically.studio.ui.components.CameraPreview

data class ChatMessage(val text: String, val isUser: Boolean)

@Composable
fun SearchScreen(
    messages: List<ChatMessage>,
    onSendText: (String) -> Unit,
    onRecordVoice: () -> Unit,
    isWearableConnected: Boolean = false
) {
    var inputText by remember { mutableStateOf("") }
    var isLivePovEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        if (isLivePovEnabled) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .height(240.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                if (isWearableConnected) {
                    POVView(modifier = Modifier.fillMaxSize())
                } else {
                    CameraPreview(modifier = Modifier.fillMaxSize())
                }
                
                Surface(
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
                ) {
                    Text(
                        text = "LIVE POV",
                        color = MaterialTheme.colorScheme.onError,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(messages) { message ->
                ChatBubble(message)
            }
        }

        InputBar(
            text = inputText,
            isLive = isLivePovEnabled,
            onTextChange = { inputText = it },
            onToggleLive = { isLivePovEnabled = !isLivePovEnabled },
            onRecordVoice = onRecordVoice,
            onSend = {
                if (inputText.isNotBlank()) {
                    onSendText(inputText)
                    inputText = ""
                }
            }
        )
    }
}

@Composable
private fun InputBar(
    text: String,
    isLive: Boolean,
    onTextChange: (String) -> Unit,
    onToggleLive: () -> Unit,
    onRecordVoice: () -> Unit,
    onSend: () -> Unit
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
            IconButton(
                onClick = onToggleLive,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = if (isLive) MaterialTheme.colorScheme.errorContainer else Color.Transparent
                )
            ) {
                Icon(
                    Icons.Default.CameraAlt, 
                    contentDescription = "Live POV",
                    tint = if (isLive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onRecordVoice) {
                Icon(Icons.Default.Mic, contentDescription = "Voice", tint = MaterialTheme.colorScheme.secondary)
            }
            TextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text("Ask Quinn to change the vibe...") },
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
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
