package com.musically.studio.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.network.SpotifyTrack
import com.musically.studio.ui.models.ChatMessage

@Composable
fun TrackItem(track: SpotifyTrack, onClick: () -> Unit = {}) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                color = Color.DarkGray,
                shape = MaterialTheme.shapes.small
            ) {}
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = track.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = track.artists.joinToString { it.name },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    onSave: (() -> Unit)? = null
) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val containerColor = if (message.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh
    val contentColor = if (message.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = containerColor,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = contentColor
                )
                if (onSave != null && !message.isUser) {
                    TextButton(
                        onClick = onSave,
                        modifier = Modifier.align(Alignment.End).padding(end = 4.dp, bottom = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.LibraryMusic,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = contentColor.copy(alpha = 0.7f)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Save Vibe",
                            style = MaterialTheme.typography.labelSmall,
                            color = contentColor.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}
