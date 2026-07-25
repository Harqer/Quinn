package com.musically.studio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueBottomSheet(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val tracks by viewModel.tracks.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding()) {
            Text(
                text = "Up Next",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            if (tracks.isEmpty()) {
                Text("Queue is empty.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                tracks.take(5).forEach { track ->
                    ListItem(
                        headlineContent = { Text(track.name, color = MaterialTheme.colorScheme.onSurface) },
                        supportingContent = { Text(track.artists.joinToString { it.name }, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
