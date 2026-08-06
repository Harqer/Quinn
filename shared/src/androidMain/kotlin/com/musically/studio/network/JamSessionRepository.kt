package com.musically.studio.network

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

enum class GameMode {
    REMIX,
    TRIVIA_NAME_THAT_TUNE,
    TRIVIA_GUESS_BY_TIME
}

enum class SessionStatus {
    LOBBY,
    IN_GAME,
    RESULTS
}

enum class TriviaState {
    BIDDING,
    PLAYING_AUDIO,
    GUESSING,
    REVEAL,
    NONE
}

data class JamParticipant(
    val uid: String = "",
    val displayName: String = "",
    val avatarUrl: String = "",
    val score: Int = 0
)

data class JamQueueItem(
    val id: String = "",
    val trackId: String = "",
    val trackName: String = "",
    val audioUrl: String = "",
    val addedByUid: String = "",
    val timestamp: Long = 0L
)

data class JamSession(
    val roomId: String = "",
    val hostId: String = "",
    val gameMode: String = GameMode.REMIX.name,
    val status: String = SessionStatus.LOBBY.name,
    val participants: Map<String, JamParticipant> = emptyMap(),
    // Used in Remix mode to store layers (e.g., "Bass", "Drums")
    val tracks: Map<String, String> = emptyMap(),
    // Used in Trivia mode to store the current target song/snippet
    val currentTriviaTrack: String = "",
    val triviaCategory: String = "All",
    val requiredNotes: Int = 0,
    val currentTriviaState: String = TriviaState.BIDDING.name,
    val currentBidderUid: String = "",
    val lowestBidNotes: Int = -1,
    val latestGuessAudioUrl: String = "",
    val latestGuessTranscription: String = "",
    val roundWinnerUid: String = "",
    val lastGuessCorrect: Boolean = false,
    val lastActualSong: String = "",
    val sharedQueue: Map<String, JamQueueItem> = emptyMap()
)

@Singleton
class JamSessionRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val jamRef = database.getReference("jam_sessions")

    suspend fun createRoom(hostId: String, hostName: String, hostAvatar: String, gameMode: GameMode, triviaCategory: String = "All"): String {
        // Generate a 5-digit room code
        var roomCode = (10000..99999).random().toString()
        
        // Very basic collision avoidance, in production we would retry or use a more robust short-id generator
        val existing = jamRef.child(roomCode).get().await()
        if (existing.exists()) {
            roomCode = (10000..99999).random().toString()
        }

        val session = JamSession(
            roomId = roomCode,
            hostId = hostId,
            gameMode = gameMode.name,
            status = SessionStatus.LOBBY.name,
            triviaCategory = triviaCategory,
            participants = mapOf(
                hostId to JamParticipant(uid = hostId, displayName = hostName, avatarUrl = hostAvatar)
            )
        )

        jamRef.child(roomCode).setValue(session).await()
        return roomCode
    }

    suspend fun joinRoom(roomCode: String, uid: String, displayName: String, avatarUrl: String): Boolean {
        val roomSnapshot = jamRef.child(roomCode).get().await()
        if (!roomSnapshot.exists()) return false

        val participant = JamParticipant(uid = uid, displayName = displayName, avatarUrl = avatarUrl)
        jamRef.child(roomCode).child("participants").child(uid).setValue(participant).await()
        return true
    }

    fun observeRoom(roomCode: String): Flow<JamSession?> = callbackFlow {
        val ref = jamRef.child(roomCode)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val session = snapshot.getValue(JamSession::class.java)
                trySend(session)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Failed to observe Jam Session $roomCode")
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun updateStatus(roomCode: String, status: SessionStatus) {
        jamRef.child(roomCode).child("status").setValue(status.name).await()
    }

    suspend fun addTrackLayer(roomCode: String, layerId: String, prompt: String) {
        jamRef.child(roomCode).child("tracks").child(layerId).setValue(prompt).await()
    }

    suspend fun setTriviaTarget(roomCode: String, trackId: String, notes: Int = 0) {
        val updates = mapOf(
            "currentTriviaTrack" to trackId,
            "requiredNotes" to notes
        )
        jamRef.child(roomCode).updateChildren(updates).await()
    }
    
    suspend fun updateTriviaState(roomCode: String, state: TriviaState) {
        jamRef.child(roomCode).child("currentTriviaState").setValue(state.name).await()
    }

    suspend fun submitTriviaGuess(roomCode: String, wasCorrect: Boolean, actualSong: String) {
        val updates = mapOf(
            "lastGuessCorrect" to wasCorrect,
            "lastActualSong" to actualSong,
            "currentTriviaState" to TriviaState.REVEAL.name
        )
        jamRef.child(roomCode).updateChildren(updates).await()
    }

    suspend fun addTrackToQueue(roomCode: String, trackId: String, trackName: String, audioUrl: String, addedByUid: String) {
        val queueId = jamRef.child(roomCode).child("sharedQueue").push().key ?: return
        val item = JamQueueItem(
            id = queueId,
            trackId = trackId,
            trackName = trackName,
            audioUrl = audioUrl,
            addedByUid = addedByUid,
            timestamp = System.currentTimeMillis()
        )
        jamRef.child(roomCode).child("sharedQueue").child(queueId).setValue(item).await()
    }
}
