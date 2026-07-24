package com.musically.studio.ui.screens

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackOptionsBottomSheet(
    trackId: String,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val tracks by viewModel.tracks.collectAsState()
    val context = LocalContext.current
    val track = tracks.find { it.id == trackId }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding()) {
            if (track != null) {
                Text(
                    text = track.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                ListItem(
                    headlineContent = { Text("Add to playlist", color = MaterialTheme.colorScheme.onSurface) },
                    leadingContent = { Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    modifier = Modifier.clickable { 
                        viewModel.addToPlaylist(track.id)
                        onDismiss()
                    }
                )
                ListItem(
                    headlineContent = { Text("View artist", color = MaterialTheme.colorScheme.onSurface) },
                    leadingContent = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    modifier = Modifier.clickable { 
                        viewModel.viewArtist(context, track)
                        onDismiss()
                    }
                )
                ListItem(
                    headlineContent = { Text("Share vibe", color = MaterialTheme.colorScheme.onSurface) },
                    leadingContent = { Icon(Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    modifier = Modifier.clickable { 
                        viewModel.shareTrack(track.id) { url ->
                            if (url != null) {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Check out this vibe on Mave: $url")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)
                            }
                        }
                        onDismiss()
                    }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
