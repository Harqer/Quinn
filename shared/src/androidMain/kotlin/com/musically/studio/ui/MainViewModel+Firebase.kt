/**
 * @AtomicLevel: Template/Page
 * @SemanticPurpose: Android Component for MainViewModel+Firebase.kt
 */

package com.musically.studio.ui

import timber.log.Timber
import com.musically.studio.network.MaveTrack
import com.musically.studio.network.MavePlaylist
import com.musically.studio.network.MaveCategory
import com.musically.studio.network.MaveAudiobook
import com.musically.studio.network.MavePodcast
import android.content.Context
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.functions
import com.google.firebase.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.catch
import com.musically.studio.network.toMaveTrack
import com.musically.studio.network.toMavePlaylist
import com.musically.studio.network.toMaveCategory
import com.musically.studio.network.toMaveAudiobook
import com.musically.studio.network.toMavePodcast
import com.musically.studio.dataconnect.*
import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers

private var audioRecordInstance: AudioRecord? = null
private var voiceRecordingJob: kotlinx.coroutines.Job? = null

fun MainViewModel.fetchPlaylists() {
    viewModelScope.launch {
        dataConnectRepository.getPlaylists()
            .onStart { _isLoading.value = true }
            .catch { e ->
                Timber.e(e, "[MAIN_VM] Failed to fetch playlists")
                _isLoading.value = false
                _catalogErrorMessage.value = e.message ?: "Failed to fetch playlists"
            }
            .collectLatest { items ->
                _playlists.value = items.map { it.toMavePlaylist() }
                _isLoading.value = false
            }
    }
}

fun MainViewModel.fetchUserTracks() {
    viewModelScope.launch {
        dataConnectRepository.getUserTracks()
            .onStart { _isLoading.value = true }
            .catch { e ->
                Timber.e(e, "[MAIN_VM] Failed to fetch user tracks")
                _isLoading.value = false
                _catalogErrorMessage.value = e.message ?: "Failed to fetch user tracks"
            }
            .collectLatest { items ->
                _tracks.value = items.map { it.toMaveTrack() }
                _isLoading.value = false
            }
    }
}

fun MainViewModel.fetchLikedTracks() {
    viewModelScope.launch {
        dataConnectRepository.getLikedTracks()
            .onStart { _isLoading.value = true }
            .catch { e ->
                Timber.e(e, "[MAIN_VM] Failed to fetch liked tracks")
                _isLoading.value = false
                _catalogErrorMessage.value = e.message ?: "Failed to fetch liked tracks"
            }
            .collectLatest { items ->
                _likedTracks.value = items.map { it.toMaveTrack() }
                _isLoading.value = false
            }
    }
}

fun MainViewModel.fetchBookmarkedTracks() {
    viewModelScope.launch {
        dataConnectRepository.getBookmarkedTracks()
            .onStart { _isLoading.value = true }
            .catch { e ->
                Timber.e(e, "[MAIN_VM] Failed to fetch bookmarked tracks")
                _isLoading.value = false
                _catalogErrorMessage.value = e.message ?: "Failed to fetch bookmarked tracks"
            }
            .collectLatest { items ->
                _bookmarkedTracks.value = items.map { it.toMaveTrack() }
                _isLoading.value = false
            }
    }
}

fun MainViewModel.fetchCommunityTracks() {
    viewModelScope.launch {
        dataConnectRepository.getCommunityTracks()
            .onStart { _isLoading.value = true }
            .catch { e ->
                Timber.e(e, "[MAIN_VM] Failed to fetch community tracks")
                _isLoading.value = false
                _catalogErrorMessage.value = e.message ?: "Failed to fetch community tracks"
            }
            .collectLatest { items ->
                _communityTracks.value = items.map { it.toMaveTrack() }
                _isLoading.value = false
            }
    }
}

fun MainViewModel.fetchCategories() {
    viewModelScope.launch {
        dataConnectRepository.getCategories()
            .onStart { _isLoading.value = true }
            .catch { e ->
                Timber.e(e, "[MAIN_VM] Failed to fetch categories")
                _isLoading.value = false
                _catalogErrorMessage.value = e.message ?: "Failed to fetch categories"
            }
            .collectLatest { items ->
                _categories.value = items.map { it.toMaveCategory() }
                _isLoading.value = false
            }
    }
}

fun MainViewModel.fetchAudiobooks() {
    viewModelScope.launch {
        dataConnectRepository.getAudiobooks()
            .onStart { _isLoading.value = true }
            .catch { e ->
                Timber.e(e, "[MAIN_VM] Failed to fetch audiobooks")
                _isLoading.value = false
                _catalogErrorMessage.value = e.message ?: "Failed to fetch audiobooks"
            }
            .collectLatest { items ->
                _audiobooks.value = items.map { it.toMaveAudiobook() }
                _isLoading.value = false
            }
    }
}

fun MainViewModel.fetchPodcasts() {
    viewModelScope.launch {
        dataConnectRepository.getPodcasts()
            .onStart { _isLoading.value = true }
            .catch { e ->
                Timber.e(e, "[MAIN_VM] Failed to fetch podcasts")
                _isLoading.value = false
                _catalogErrorMessage.value = e.message ?: "Failed to fetch podcasts"
            }
            .collectLatest { items ->
                _podcasts.value = items.map { it.toMavePodcast() }
                _isLoading.value = false
            }
    }
}

fun MainViewModel.generateMusicPrompts(query: String) {
    Timber.d("[MAIN_VM] Generating music prompts for query: %s", query)
    sendTextCommand("Compose music prompt for: $query")
}

fun MainViewModel.sendTextCommand(text: String) {
    viewModelScope.launch {
        Timber.d("[MAIN_VM] Sending text command to Gemini Live Manager: %s", text)
        try {
            geminiLiveManager.sendTextMessage(text)
        } catch (e: Exception) {
            Timber.e(e, "[MAIN_VM] Failed to send text command")
        }
    }
}

fun MainViewModel.generateCoverMedia(trackId: String?, customPrompt: String, apiType: String, onComplete: (String?) -> Unit) {
    viewModelScope.launch {
        Timber.d("[MAIN_VM] Generating cover media with prompt: %s, apiType: %s", customPrompt, apiType)
        try {
            val functions = com.google.firebase.Firebase.functions
            val result = functions.getHttpsCallableFromUrl(java.net.URL("https://musically-studio.firebaseapp.com/api/generateVisualMedia"))
                .call(
                    mapOf(
                        "trackId" to trackId,
                        "preset" to customPrompt,
                        "intent" to apiType
                    )
                ).await()
            
            @Suppress("UNCHECKED_CAST")
            val data = result.data as? Map<String, Any>
            val url = data?.get("url") as? String
            Timber.d("[MAIN_VM] Received media URL: %s", url)
            onComplete(url)
        } catch (e: Exception) {
            Timber.e(e, "[MAIN_VM] Failed to generate cover media")
            onComplete(null)
        }
    }
}

fun MainViewModel.clearCatalogError() {
    Timber.d("[MAIN_VM] Clearing catalog error state")
    _catalogErrorMessage.value = null
}

fun MainViewModel.clearLiveSessionHistory() {
    Timber.d("[MAIN_VM] Clearing live session history")
    messages.clear()
}

fun MainViewModel.generateLyrics(trackId: String, audioUrl: String?) {
    viewModelScope.launch {
        Timber.d("[MAIN_VM] Generating lyrics for track: %s", trackId)
        _lyrics.value = null // Reset before fetch
        try {
            val functions = com.google.firebase.Firebase.functions
            val result = functions.getHttpsCallableFromUrl(java.net.URL("https://musically-studio.firebaseapp.com/api/generateLyrics"))
                .call(
                    mapOf(
                        "trackId" to trackId,
                        "audioUrl" to audioUrl
                    )
                ).await()
            
            @Suppress("UNCHECKED_CAST")
            val data = result.data as? Map<String, Any>
            val lyrics = data?.get("lyrics") as? String
            Timber.d("[MAIN_VM] Received lyrics, length=%d", lyrics?.length ?: 0)
            // Update the _lyrics StateFlow so LyricsBottomSheet can observe and render them.
            _lyrics.value = lyrics ?: "No lyrics available for this track."
        } catch (e: Exception) {
            Timber.e(e, "[MAIN_VM] Failed to generate lyrics")
            _lyrics.value = "Failed to generate lyrics. Please try again."
        }
    }
}

@android.annotation.SuppressLint("MissingPermission")
fun MainViewModel.recordVoice(context: Context?) {
    if (context == null) return

    if (_isRecording.value) {
        stopRecording()
        return
    }

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
        Timber.e("[MAIN_VM] Missing RECORD_AUDIO permission")
        return
    }

    val sampleRate = 16000
    val channelConfig = AudioFormat.CHANNEL_IN_MONO
    val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    try {
        audioRecordInstance = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        audioRecordInstance?.startRecording()
        _isRecording.value = true
        Timber.d("[MAIN_VM] Initiating voice recording session")

        voiceRecordingJob = viewModelScope.launch(Dispatchers.IO) {
            val buffer = ByteArray(bufferSize)
            while (_isRecording.value) {
                val read = audioRecordInstance?.read(buffer, 0, bufferSize) ?: 0
                if (read > 0) {
                    val pcmData = buffer.copyOfRange(0, read)
                    geminiLiveManager.sendAudio(pcmData)
                }
            }
        }
    } catch (e: Exception) {
        Timber.e(e, "[MAIN_VM] Failed to start recording")
        _isRecording.value = false
    }
}

fun MainViewModel.generatePodcast(topic: String) {
    viewModelScope.launch {
        Timber.d("[MAIN_VM] Generating podcast for topic: %s", topic)
        _isLoading.value = true
        try {
            // 1. Generate Script
            val result = Firebase.functions.getHttpsCallableFromUrl(java.net.URL("https://musically-studio.firebaseapp.com/api/generatePodcastScript")).call(mapOf("topic" to topic)).await()
            @Suppress("UNCHECKED_CAST")
            val data = result.data as? Map<String, Any>
            val script = data?.get("script") as? String
            Timber.d("[MAIN_VM] Generated Podcast Script length: %d", script?.length ?: 0)
            
            if (!script.isNullOrBlank()) {
                messages.add(com.musically.studio.ui.models.ChatMessage("Generated script. Now synthesizing audio via VibeVoice...", false))
                
                // 2. Synthesize Audio
                val audioResult = Firebase.functions.getHttpsCallableFromUrl(java.net.URL("https://musically-studio.firebaseapp.com/api/generatePodcastAudio"))
                    .withTimeout(540, java.util.concurrent.TimeUnit.SECONDS) // VibeVoice takes a while
                    .call(mapOf("scriptData" to data))
                    .await()
                
                @Suppress("UNCHECKED_CAST")
                val audioData = audioResult.data as? Map<String, Any>
                val audioUrl = audioData?.get("audioUrl") as? String
                
                if (audioUrl != null) {
                    // 3. Persist via DataConnect
                    val publishDate = com.google.firebase.Timestamp.now()
                    val success = dataConnectRepository.savePodcastEpisode(
                        showId = "1", 
                        title = topic, 
                        description = script.take(200),
                        audioUrl = audioUrl,
                        durationMs = 0,
                        publishDate = publishDate
                    )
                    
                    if (success) {
                        messages.add(com.musically.studio.ui.models.ChatMessage("Audio synthesized successfully! Available in your podcasts.", false))
                    } else {
                        messages.add(com.musically.studio.ui.models.ChatMessage("Audio generated but failed to save to database.", false))
                    }
                } else {
                    messages.add(com.musically.studio.ui.models.ChatMessage("Failed to synthesize audio.", false))
                }
            } else {
                // Surface an explicit empty state — never silently succeed with no content.
                messages.add(com.musically.studio.ui.models.ChatMessage(
                    "The podcast script returned empty. Please try a more specific topic.", false
                ))
            }
        } catch (e: Exception) {
            Timber.e(e, "[MAIN_VM] Failed to generate podcast via Firebase Functions")
            // Zero Silent Fallback policy: always surface errors to the user.
            val userMessage = when {
                e.message?.contains("429") == true -> "Podcast generation quota exceeded. Please try again in a moment."
                e.message?.contains("503") == true -> "Podcast service is temporarily unavailable. Please try again."
                else -> "Failed to generate podcast. Please check your connection and try again."
            }
            messages.add(com.musically.studio.ui.models.ChatMessage(userMessage, false))
            _catalogErrorMessage.value = userMessage
        } finally {
            _isLoading.value = false
        }
    }
}

/**
 * Adds [trackId] to the specified playlist.
 * If [playlistId] is not provided, it falls back to bookmarking the track.
 */
fun MainViewModel.addToPlaylist(playlistId: String, trackId: String) {
    viewModelScope.launch {
        Timber.d("[MAIN_VM] Adding track %s to playlist %s", trackId, playlistId)
        try {
            val success = dataConnectRepository.addTrackToPlaylist(trackId, playlistId)
            if (success) {
                Timber.d("[MAIN_VM] Successfully added track %s to playlist %s", trackId, playlistId)
            } else {
                Timber.w("[MAIN_VM] addTrackToPlaylist returned false for track %s", trackId)
                _catalogErrorMessage.value = "Couldn't add to playlist. Please try again."
            }
        } catch (e: Exception) {
            Timber.e(e, "[MAIN_VM] Failed to add track %s to playlist", trackId)
            _catalogErrorMessage.value = "Couldn't add to playlist. Please try again."
        }
    }
}

fun MainViewModel.viewArtist(context: Context, track: MaveTrack) {
    val artistId = track.artists.firstOrNull()?.id
    if (artistId != null) {
        navigateTo(com.musically.studio.ui.navigation.Route.UserProfile(artistId))
    } else {
        Timber.w("No artist ID found for track %s", track.id)
    }
}

fun MainViewModel.sendFrame(base64: String) {
    viewModelScope.launch {
        try {
            geminiLiveManager.sendVideoFrame(base64)
        } catch (e: Exception) {
            Timber.e(e, "[MAIN_VM] Failed to send frame")
        }
    }
}

fun MainViewModel.fetchCatalog() {
    viewModelScope.launch {
        Timber.d("[MAIN_VM] Fetching music catalog")
        fetchCategories()
        fetchAudiobooks()
        fetchPodcasts()
        fetchCommunityTracks()
    }
}

/**
 * Loads the home sections layout from DataConnect.
 *
 * This is a suspend function so the UI can call it within a [LaunchedEffect] while
 * keeping DataConnect access inside the ViewModel (MVVM compliance — screens must not
 * access DataConnect directly).
 *
 * @return the list of home section definitions, or an empty list on error.
 */
suspend fun MainViewModel.loadHomeSections(): List<com.musically.studio.dataconnect.ListHomeSectionsQuery.Data.HomeSectionsItem> {
    return try {
        val result = com.musically.studio.dataconnect.DefaultConnector.instance.listHomeSections.execute()
        result.data.homeSections
    } catch (e: Exception) {
        Timber.e(e, "[MAIN_VM] Failed to load home sections")
        emptyList()
    }
}


fun MainViewModel.stopRecording() {
    Timber.d("[MAIN_VM] Stopping voice recording session")
    _isRecording.value = false
    audioRecordInstance?.stop()
    audioRecordInstance?.release()
    audioRecordInstance = null
    voiceRecordingJob?.cancel()
    voiceRecordingJob = null
}

fun MainViewModel.onGalleryImageSelected(base64: String) {
    sendFrame(base64)
}

fun MainViewModel.sendVisionFrame(base64: String, mimeType: String = "image/jpeg") {
    viewModelScope.launch {
        try {
            geminiLiveManager.sendVideoFrame(base64, mimeType)
        } catch (e: Exception) {
            Timber.e(e, "[MAIN_VM] Failed to send vision frame")
        }
    }
}

fun MainViewModel.fetchVibesByUserId(userId: String) {
    Timber.d("[MAIN_VM] Fetching vibes for user: %s", userId)
    viewModelScope.launch {
        try {
            _isLoading.value = true
            com.google.firebase.Firebase.functions.getHttpsCallableFromUrl(java.net.URL("https://musically-studio.firebaseapp.com/api/fetchPersonalizedSpotifyVibe"))
                .call(mapOf("vibeQuery" to "chill", "playlistId" to "37i9dQZEVXbMDoHDwVN2tF")).await()
            fetchCommunityTracks()
            fetchUserTracks()
        } catch (e: Exception) {
            Timber.e(e, "[MAIN_VM] Failed to fetch personalized Spotify vibe")
            fetchCommunityTracks()
        } finally {
            _isLoading.value = false
        }
    }
}
