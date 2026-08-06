package com.musically.studio.ui.components.molecules
import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.models.ChatMessage

@Composable
fun ChatBubble(message: ChatMessage, viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val tracks by viewModel.tracks.collectAsStateWithLifecycle()
    val track = tracks.find { it.id == message.trackId }
    val bgColor = remember(message.trackId) {
        val hash = kotlin.math.abs(message.trackId?.hashCode() ?: message.hashCode())
        val r = (hash and 0xFF0000) shr 16
        val g = (hash and 0x00FF00) shr 8
        val b = hash and 0x0000FF
        Color(r, g, b)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            val imageUrl = track?.album?.images?.firstOrNull()?.url
            if (imageUrl != null) {
                coil.compose.AsyncImage(
                    model = imageUrl,
                    contentDescription = "Track Art",
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.size(32.dp).clip(CircleShape)
                )
            } else {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = bgColor.copy(alpha = 0.5f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.MusicNote, contentDescription = null,
                        tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = if (message.isUser) 16.dp else 4.dp,
                topEnd = if (message.isUser) 4.dp else 16.dp,
                bottomStart = 16.dp, bottomEnd = 16.dp
            ),
            color = if (message.isUser) MaterialTheme.colorScheme.primary else bgColor.copy(alpha = 0.3f)
        ) {
            Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            androidx.compose.foundation.text.selection.SelectionContainer {
                Text(text = message.text, style = MaterialTheme.typography.bodyMedium,
                    color = if (message.isUser) Color.White else com.musically.studio.ui.theme.MaveBlueGray200)
            }
        }
    }
}
