package com.musically.studio.ui.screens
import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.searchCatalog
import com.musically.studio.ui.components.atoms.MaveTextField
import com.musically.studio.ui.components.molecules.PlaylistTrackItem
import com.musically.studio.ui.theme.MaveStyles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JamSongPickerScreen(
    mainViewModel: MainViewModel,
    jamViewModel: JamViewModel,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val searchResults by mainViewModel.searchResults.collectAsStateWithLifecycle()

    LaunchedEffect(query) {
        if (query.isNotEmpty()) {
            mainViewModel.searchCatalog(query)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Add to queue", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            MaveTextField(
                value = query,
                onValueChange = { query = it },
                label = "Search songs",
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(searchResults, key = { it.id }) { track ->
                    val interactionSource = remember { MutableInteractionSource() }
                    val styleState = rememberUpdatedStyleState(interactionSource) {}
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .styleable(styleState, MaveStyles.musicTrackCardStyle, androidx.compose.foundation.style.Style),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            PlaylistTrackItem(
                                track = track,
                                trackNumber = 0,
                                onClick = { 
                                    jamViewModel.enqueueTrack(track)
                                    onDismiss()
                                },
                                onMoreClick = {
                                    jamViewModel.enqueueTrack(track)
                                    onDismiss()
                                }
                            )
                        }
                        IconButton(onClick = {
                            jamViewModel.enqueueTrack(track)
                            onDismiss()
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
