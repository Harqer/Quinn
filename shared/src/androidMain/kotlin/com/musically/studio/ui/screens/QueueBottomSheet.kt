package com.musically.studio.ui.screens
import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.*
import com.musically.studio.ui.utils.debouncedClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueBottomSheet(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val queue by viewModel.queue.collectAsState()
    val queueIndex by viewModel.queueIndex.collectAsState()

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
            
            if (queue.isEmpty()) {
                Text("Queue is empty.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                queue.forEachIndexed { index, track ->
                    val isCurrent = index == queueIndex
                    ListItem(
                        headlineContent = { 
                            Text(
                                track.name, 
                                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                            ) 
                        },
                        supportingContent = { Text(track.artists.joinToString { it.name }, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.debouncedClickable {
                            viewModel.playQueue(queue, index)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
