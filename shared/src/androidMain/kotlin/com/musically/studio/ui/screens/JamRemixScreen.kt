package com.musically.studio.ui.screens
import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musically.studio.network.JamSession
import com.musically.studio.ui.components.atoms.MaveButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JamRemixScreen(
    session: JamSession,
    instruments: List<com.musically.studio.dataconnect.ListInstrumentsQuery.Data.InstrumentsItem>,
    onAddInstrument: (String) -> Unit,
    onEndJam: () -> Unit,
    localUserId: String
) {
    // Dynamically filter available instruments to exclude those already active in the session
    val allInstrumentNames = instruments.map { it.name }
    val activeInstruments = session.tracks.values.toSet()
    val availableInstruments = allInstrumentNames.filter { it !in activeInstruments }
    
    // For demo purposes, assuming session.participantIds dictates some active tracks
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Live Remix: ${session.roomId}") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Active Stacks",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Render Kahoot-style active stacks
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val trackEntries = session.tracks.toList()
                if (trackEntries.isEmpty()) {
                    item {
                        Text(
                            text = "No active track layers. Add an instrument below!",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                } else {
                    items(trackEntries, key = { it.first }) { (uid, instrument) ->
                        val participantName = session.participants[uid]?.displayName?.ifBlank { null }
                        val displayName = if (uid == localUserId) {
                            "You ($instrument)"
                        } else {
                            "${participantName ?: "Player ${uid.take(4)}"} ($instrument)"
                        }

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = displayName,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Playing...",
                                    color = Color.Green,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Add Your Instrument",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Render grid of remaining available instruments
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (availableInstruments.isEmpty()) {
                    Text(
                        text = "All instruments are currently in use!",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                } else {
                    availableInstruments.chunked(3).forEach { rowInstruments ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowInstruments.forEach { instrument ->
                                MaveButton(
                                    text = instrument,
                                    onClick = { onAddInstrument(instrument) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // Add empty spacers if the row has fewer than 3 items to keep consistent width
                            val emptySlots = 3 - rowInstruments.size
                            repeat(emptySlots) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            MaveButton(
                text = "End Jam",
                onClick = onEndJam,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
