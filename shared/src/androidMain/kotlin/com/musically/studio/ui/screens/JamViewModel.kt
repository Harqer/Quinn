package com.musically.studio.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.musically.studio.network.GameMode
import com.musically.studio.network.JamSession
import com.musically.studio.network.JamSessionRepository
import com.musically.studio.network.SessionStatus
import com.musically.studio.network.GeminiLiveManager
import com.musically.studio.service.LiveApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import com.musically.studio.audio.TriviaAudioController
import com.musically.studio.network.TriviaState
import androidx.core.content.ContextCompat
import com.musically.studio.audio.StreamAudioPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlinx.coroutines.flow.first

data class JamUiState(
    val currentRoomCode: String? = null,
    val session: JamSession? = null,
    val isHost: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class JamViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: JamSessionRepository,
    private val dataConnectRepository: com.musically.studio.data.repository.DataConnectRepository,
    private val auth: FirebaseAuth,
    private val liveApiService: LiveApiService,
    private val geminiLiveManager: GeminiLiveManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(JamUiState())
    val uiState: StateFlow<JamUiState> = _uiState.asStateFlow()

    private val _instruments = MutableStateFlow<List<com.musically.studio.dataconnect.ListInstrumentsQuery.Data.InstrumentsItem>>(emptyList())
    val instruments: StateFlow<List<com.musically.studio.dataconnect.ListInstrumentsQuery.Data.InstrumentsItem>> = _instruments.asStateFlow()

    private val currentUser get() = auth.currentUser
    
    private val streamAudioPlayer = StreamAudioPlayer()
    private val triviaAudioController = TriviaAudioController(context)
    private var isRecordingVoice = false
    private var recordingJob: kotlinx.coroutines.Job? = null
    private var audioRecord: AudioRecord? = null

    init {
        viewModelScope.launch {
            dataConnectRepository.listInstruments().collect { data ->
                _instruments.update { data }
            }
        }
        viewModelScope.launch {
            geminiLiveManager.audioOutput.collect { pcmData ->
                streamAudioPlayer.queueAudioChunk(pcmData)
            }
        }
        viewModelScope.launch {
            geminiLiveManager.functionCalls.collect { call ->
                val name = call.optString("name")
                val args = call.optJSONObject("args")
                if (name == "validate_trivia_guess" && args != null) {
                    val wasCorrect = args.optBoolean("was_correct", false)
                    val actualSong = args.optString("actual_song", "Unknown Song")
                    submitTriviaGuess(wasCorrect, actualSong)
                    
                    val callId = call.optString("id")
                    if (callId.isNotBlank()) {
                        geminiLiveManager.sendResponse(callId, name, org.json.JSONObject().apply { put("success", true) })
                    }
                }
            }
        }
    }

    fun hostGame(gameMode: GameMode, triviaCategory: String = "All") {
        val user = currentUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val roomCode = repository.createRoom(
                    hostId = user.uid,
                    hostName = user.displayName ?: "Host",
                    hostAvatar = user.photoUrl?.toString() ?: "",
                    gameMode = gameMode,
                    triviaCategory = triviaCategory
                )
                _uiState.update { it.copy(currentRoomCode = roomCode, isHost = true, isLoading = false) }
                observeRoom(roomCode)
            } catch (e: Exception) {
                Timber.e(e, "Failed to host game")
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun joinGame(roomCode: String) {
        val user = currentUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val success = repository.joinRoom(
                    roomCode = roomCode,
                    uid = user.uid,
                    displayName = user.displayName ?: "Player",
                    avatarUrl = user.photoUrl?.toString() ?: ""
                )
                if (success) {
                    _uiState.update { it.copy(currentRoomCode = roomCode, isHost = false, isLoading = false) }
                    observeRoom(roomCode)
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Room not found") }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to join game")
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun observeRoom(roomCode: String) {
        viewModelScope.launch {
            repository.observeRoom(roomCode).collect { session ->
                _uiState.update { it.copy(session = session) }
            }
        }
    }

    fun startGame() {
        val roomCode = _uiState.value.currentRoomCode ?: return
        viewModelScope.launch {
            repository.updateStatus(roomCode, SessionStatus.IN_GAME)
        }
    }

    fun finishGame() {
        val roomCode = _uiState.value.currentRoomCode ?: return
        val session = _uiState.value.session ?: return
        viewModelScope.launch {
            repository.updateStatus(roomCode, SessionStatus.RESULTS)
            dataConnectRepository.createJamSessionHistory(
                roomId = roomCode,
                gameMode = session.gameMode,
                participantCount = session.participants.size
            )
        }
    }

    fun addLayer(prompt: String) {
        val roomCode = _uiState.value.currentRoomCode ?: return
        val uid = currentUser?.uid ?: return
        viewModelScope.launch {
            repository.addTrackLayer(roomCode, uid, prompt)
            try {
                val result = liveApiService.executeTool("tweak_instrumentation", mapOf("prompt" to prompt))
                val audioUrl = result.optString("audioUrl")
                if (audioUrl.isNotBlank()) {
                    withContext(kotlinx.coroutines.Dispatchers.Main) {
                        try {
                            val player = android.media.MediaPlayer()
                            player.setDataSource(audioUrl)
                            player.setOnPreparedListener { it.start() }
                            player.setOnCompletionListener { it.release() }
                            player.prepareAsync()
                        } catch (e: Exception) {
                            Timber.e(e, "MediaPlayer setup failed")
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to tweak instrumentation")
            }
        }
    }

    fun enqueueTrack(track: com.musically.studio.network.MaveTrack) {
        val roomCode = _uiState.value.currentRoomCode ?: return
        val uid = currentUser?.uid ?: return
        viewModelScope.launch {
            repository.addTrackToQueue(roomCode, track.id, track.name, track.audioUrl ?: "", uid)
        }
    }

    fun setTriviaTarget(trackId: String, notes: Int) {
        val roomCode = _uiState.value.currentRoomCode ?: return
        viewModelScope.launch {
            repository.setTriviaTarget(roomCode, trackId, notes)
        }
    }

    fun startTriviaSession(actualSong: String) {
        val systemInstruction = "You are a Trivia Game Host for Name That Tune. The correct song is '$actualSong'. Users will guess the song by singing or speaking the name. You must validate their guesses against the correct song by calling the validate_trivia_guess function as soon as you have an answer."
        geminiLiveManager.connect(resume = false, systemInstruction = systemInstruction)
    }

    fun playTriviaSnippet(notes: Int) {
        val roomCode = _uiState.value.currentRoomCode ?: return
        val currentTrack = _uiState.value.session?.currentTriviaTrack ?: "Unknown Song"
        viewModelScope.launch {
            repository.updateTriviaState(roomCode, TriviaState.PLAYING_AUDIO)
            try {
                val result = liveApiService.executeTool("generate_full_track", mapOf("prompt" to "A recognizable song snippet with exactly $notes notes"))
                val audioUrl = result.optString("audioUrl")
                if (audioUrl.isNotBlank()) {
                    // Approximate ~500ms per note
                    val durationMs = (notes * 500).toLong()
                    withContext(kotlinx.coroutines.Dispatchers.Main) {
                        triviaAudioController.playSnippet(audioUrl, durationMs) {
                            // After playing, transition to guessing and connect Gemini Live
                            viewModelScope.launch {
                                startTriviaSession(currentTrack)
                                repository.updateTriviaState(roomCode, TriviaState.GUESSING)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to generate trivia snippet")
            }
        }
    }
    
    fun submitTriviaGuess(wasCorrect: Boolean, actualSong: String) {
        val roomCode = _uiState.value.currentRoomCode ?: return
        viewModelScope.launch {
            repository.submitTriviaGuess(roomCode, wasCorrect, actualSong)
        }
    }
    
    fun nextTriviaRound() {
        val roomCode = _uiState.value.currentRoomCode ?: return
        val category = _uiState.value.session?.triviaCategory ?: "All"
        viewModelScope.launch {
            repository.updateTriviaState(roomCode, TriviaState.BIDDING)
            try {
                val tracks = dataConnectRepository.getCommunityTracks().first()
                val filteredTracks = if (category != "All") {
                    tracks.filter { it.prompt?.contains(category, ignoreCase = true) == true }
                } else tracks
                
                val selectedTrack = if (filteredTracks.isNotEmpty()) {
                    filteredTracks.random()
                } else {
                    tracks.randomOrNull()
                }
                
                if (selectedTrack != null) {
                    // Set the actual song title as the trivia target
                    repository.setTriviaTarget(roomCode, selectedTrack.title, 0)
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to fetch tracks for trivia round")
            }
        }
    }

    @android.annotation.SuppressLint("MissingPermission")
    fun recordVoice(context: Context, isListening: Boolean) {
        if (!isListening) {
            isRecordingVoice = false
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
            recordingJob?.cancel()
            return
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Timber.e("Missing RECORD_AUDIO permission")
            return
        }

        val sampleRate = 16000
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )

            audioRecord?.startRecording()
            isRecordingVoice = true

            recordingJob = viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                val buffer = ByteArray(bufferSize)
                while (isRecordingVoice) {
                    val read = audioRecord?.read(buffer, 0, bufferSize) ?: 0
                    if (read > 0) {
                        val pcmData = buffer.copyOfRange(0, read)
                        geminiLiveManager.sendAudio(pcmData)
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to start recording")
        }
    }
}
