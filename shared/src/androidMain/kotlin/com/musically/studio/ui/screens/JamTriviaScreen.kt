/**
 * @AtomicLevel: Template/Page
 * @SemanticPurpose: Android Component for JamTriviaScreen.kt
 */

package com.musically.studio.ui.screens
import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.musically.studio.network.GameMode
import com.musically.studio.network.JamSession
import com.musically.studio.network.TriviaState
import com.musically.studio.ui.components.organisms.*

@Composable
fun JamTriviaScreen(
    session: JamSession,
    onBid: (Int) -> Unit,
    onPass: () -> Unit,
    onPlayAudio: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onNextRound: () -> Unit,
    onEndGame: () -> Unit,
    isRecording: Boolean,
    isPlaying: Boolean,
    wasCorrect: Boolean,
    actualSong: String,
    localUserId: String
) {
    val isLocalUserTurn = session.currentBidderUid == localUserId
    
    // Parse enum from string safely
    val triviaState = try {
        TriviaState.valueOf(session.currentTriviaState)
    } catch (e: Exception) {
        TriviaState.NONE
    }
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (triviaState) {
                TriviaState.BIDDING -> {
                    TriviaBiddingOrganism(
                        session = session,
                        currentBid = session.lowestBidNotes,
                        isLocalUserTurn = isLocalUserTurn,
                        onBid = onBid,
                        onPass = onPass
                    )
                }
                TriviaState.PLAYING_AUDIO -> {
                    TriviaListeningOrganism(
                        notesToPlay = session.lowestBidNotes,
                        isPlaying = isPlaying,
                        onPlayAudio = onPlayAudio
                    )
                }
                TriviaState.GUESSING -> {
                    TriviaGuessingOrganism(
                        isRecording = isRecording,
                        onStartRecording = onStartRecording,
                        onStopRecording = onStopRecording
                    )
                }
                TriviaState.REVEAL -> {
                    TriviaResultsOrganism(
                        wasCorrect = wasCorrect,
                        actualSong = actualSong,
                        onNextRound = onNextRound,
                        onEndGame = onEndGame
                    )
                }
                TriviaState.NONE -> {
                    // Fallback or waiting state
                }
            }
        }
    }
}
