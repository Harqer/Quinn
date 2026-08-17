/**
 * @AtomicLevel: Organism
 * @SemanticPurpose: Android Component for JamLobbyJoinOrganism.kt
 */

package com.musically.studio.ui.components.organisms
import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.network.GameMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JamLobbyJoinOrganism(
    isLoading: Boolean,
    error: String?,
    onJoinGame: (String) -> Unit,
    onHostGame: (GameMode, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var joinPin by remember { mutableStateOf("") }
    var selectedMode by remember { mutableStateOf(GameMode.REMIX) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            "Join a Jam",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = joinPin,
            onValueChange = { joinPin = it },
            label = { Text("Enter PIN") },
            singleLine = true
        )
        Button(
            onClick = { onJoinGame(joinPin) },
            modifier = Modifier.fillMaxWidth(0.8f),
            enabled = joinPin.isNotBlank() && !isLoading
        ) {
            Text("Join Game")
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        Text(
            "Or Host a New Game",
            style = MaterialTheme.typography.titleLarge
        )

        Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = selectedMode == GameMode.REMIX,
                onClick = { selectedMode = GameMode.REMIX },
                label = { Text("Remix Mode") }
            )
            FilterChip(
                selected = selectedMode == GameMode.TRIVIA_NAME_THAT_TUNE,
                onClick = { selectedMode = GameMode.TRIVIA_NAME_THAT_TUNE },
                label = { Text("Guess By Notes") }
            )
            FilterChip(
                selected = selectedMode == GameMode.TRIVIA_GUESS_BY_TIME,
                onClick = { selectedMode = GameMode.TRIVIA_GUESS_BY_TIME },
                label = { Text("Guess By 2 Seconds") }
            )
        }

        var selectedCategory by remember { mutableStateOf("All") }
        if (selectedMode.name.startsWith("TRIVIA")) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Trivia Category", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = selectedCategory == "All",
                    onClick = { selectedCategory = "All" },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = selectedCategory == "U.S. Top Tracks",
                    onClick = { selectedCategory = "U.S. Top Tracks" },
                    label = { Text("U.S. Top Tracks") }
                )
            }
        }

        Button(
            onClick = { onHostGame(selectedMode, selectedCategory) },
            modifier = Modifier.fillMaxWidth(0.8f),
            enabled = !isLoading
        ) {
            Text("Host Game")
        }

        if (error != null) {
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }
    }
}
