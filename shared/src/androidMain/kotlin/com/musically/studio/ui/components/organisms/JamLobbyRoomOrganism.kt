/**
 * @AtomicLevel: Organism
 * @SemanticPurpose: Android Component for JamLobbyRoomOrganism.kt
 */

package com.musically.studio.ui.components.organisms
import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.musically.studio.network.JamParticipant
import androidx.compose.foundation.style.styleable

@Composable
fun JamLobbyRoomOrganism(
    roomCode: String,
    gameMode: String,
    triviaCategory: String = "All",
    players: List<JamParticipant>,
    isHost: Boolean,
    onStartGame: () -> Unit,
    sharedQueue: List<com.musically.studio.network.JamQueueItem>,
    onAddTrackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Text("Room Code", style = MaterialTheme.typography.titleMedium)
        Text(
            text = roomCode,
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            letterSpacing = 8.sp
        )
        Text("Game Mode: $gameMode", style = MaterialTheme.typography.bodyLarge)
        if (gameMode.startsWith("TRIVIA")) {
            Text("Category: $triviaCategory", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Players", style = MaterialTheme.typography.titleLarge)
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(players, key = { it.uid }) { player ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    if (player.avatarUrl.isNotEmpty()) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(48.dp)
                        ) {
                            AsyncImage(
                                model = player.avatarUrl,
                                contentDescription = "Avatar",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = null)
                            }
                        }
                    }
                    Text(player.displayName, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Up Next (Shared Queue)", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = onAddTrackClick) {
                Text("Add to queue")
            }
        }
        
        if (sharedQueue.isEmpty()) {
            Text("The queue is empty.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(sharedQueue.sortedBy { it.timestamp }, key = { it.id }) { item ->
                    val interactionSource = androidx.compose.runtime.remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                    val styleState = androidx.compose.foundation.style.rememberUpdatedStyleState(interactionSource) {}
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .styleable(styleState, com.musically.studio.ui.theme.MaveStyles.musicTrackCardStyle, androidx.compose.foundation.style.Style)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.trackName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            Text("Added by ${players.find { it.uid == item.addedByUid }?.displayName ?: "Unknown"}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isHost) {
            Button(
                onClick = onStartGame,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start Jam!")
            }
        } else {
            Text("Waiting for host to start...", modifier = Modifier.align(Alignment.CenterHorizontally))
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}
