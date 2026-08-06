package com.musically.studio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.musically.studio.network.GameMode
import com.musically.studio.network.SessionStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JamLobbyScreen(
    viewModel: JamViewModel,
    onNavigateBack: () -> Unit,
    onGameStarted: (GameMode) -> Unit,
    onAddTrackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var joinPin by remember { mutableStateOf("") }
    var selectedMode by remember { mutableStateOf(GameMode.REMIX) }

    // If game has started, notify parent to navigate
    LaunchedEffect(uiState.session?.status) {
        if (uiState.session?.status == SessionStatus.IN_GAME.name) {
            val mode = GameMode.valueOf(uiState.session?.gameMode ?: GameMode.REMIX.name)
            onGameStarted(mode)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jam Lobby") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            if (uiState.currentRoomCode == null) {
                // Not in a room yet
                com.musically.studio.ui.components.organisms.JamLobbyJoinOrganism(
                    isLoading = uiState.isLoading,
                    error = uiState.error,
                    onJoinGame = { pin -> viewModel.joinGame(pin) },
                    onHostGame = { mode, category -> viewModel.hostGame(mode, category) }
                )
            } else {
                // In a room
                com.musically.studio.ui.components.organisms.JamLobbyRoomOrganism(
                    roomCode = uiState.currentRoomCode ?: "",
                    gameMode = uiState.session?.gameMode ?: "",
                    triviaCategory = uiState.session?.triviaCategory ?: "All",
                    players = uiState.session?.participants?.values?.toList() ?: emptyList(),
                    isHost = uiState.isHost,
                    sharedQueue = uiState.session?.sharedQueue?.values?.toList() ?: emptyList(),
                    onAddTrackClick = onAddTrackClick,
                    onStartGame = { viewModel.startGame() }
                )
            }
        }
    }
}
