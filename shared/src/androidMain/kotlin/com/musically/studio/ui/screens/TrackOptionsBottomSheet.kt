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
                
                val headlineAdd = "Add to playlist"
                val addIcon = Icons.Default.Add
                ListItem(
                    headlineContent = { Text(headlineAdd, color = MaterialTheme.colorScheme.onSurface) },
                    leadingContent = { Icon(addIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    modifier = Modifier.clickable { 
                        viewModel.addToPlaylist(track.id)
                        onDismiss()
                    }
                )

                val headlineArtist = "View artist"
                val personIcon = Icons.Default.Person
                ListItem(
                    headlineContent = { Text(headlineArtist, color = MaterialTheme.colorScheme.onSurface) },
                    leadingContent = { Icon(personIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    modifier = Modifier.clickable { 
                        viewModel.viewArtist(context, track)
                        onDismiss()
                    }
                )
                
                val onShare: () -> Unit = {
                    viewModel.shareTrack(track.id) { url ->
                        if (url != null) {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Check out this song on Mave: $url")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, null))
                        }
                    }
                    onDismiss()
                }

                val headlineShare = "Share song"
                val shareIcon = Icons.Default.Share
                ListItem(
                    headlineContent = { Text(headlineShare, color = MaterialTheme.colorScheme.onSurface) },
                    leadingContent = { Icon(shareIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    modifier = Modifier.clickable(onClick = onShare)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
