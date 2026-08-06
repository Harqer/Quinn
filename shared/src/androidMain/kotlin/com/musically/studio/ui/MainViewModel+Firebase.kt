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
            val result = functions.getHttpsCallable("generateVisualMedia")
                .call(
                    mapOf(
                        "trackId" to trackId,
                        "preset" to customPrompt,
                        "intent" to apiType
                    )
                ).await()
            
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
        try {
            val functions = com.google.firebase.Firebase.functions
            val result = functions.getHttpsCallable("generateLyrics")
                .call(
                    mapOf(
                        "trackId" to trackId,
                        "audioUrl" to audioUrl
                    )
                ).await()
            
            val data = result.data as? Map<String, Any>
            val lyrics = data?.get("lyrics") as? String
            Timber.d("[MAIN_VM] Received lyrics")
            // Here you would typically update the track object in the UI state with the new lyrics
        } catch (e: Exception) {
            Timber.e(e, "[MAIN_VM] Failed to generate lyrics")
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
        try {
            val result = Firebase.functions.getHttpsCallable("generatePodcastScript").call(mapOf("topic" to topic)).await()
            val data = result.data as? Map<*, *>
            val script = data?.get("script") as? String
            Timber.d("[MAIN_VM] Generated Podcast Script: %s", script)
            if (script != null) {
                // In a full implementation, you would update the UI state with the script.
                // Assuming you have a way to add to messages:
                messages.add(com.musically.studio.ui.models.ChatMessage(script, false))
            }
        } catch (e: Exception) {
            Timber.e(e, "[MAIN_VM] Failed to generate podcast via Firebase Functions")
        }
    }
}

fun MainViewModel.addToPlaylist(trackId: String) {
    viewModelScope.launch {
        Timber.d("[MAIN_VM] Adding track %s to playlist", trackId)
        try {
            dataConnectRepository.bookmarkTrack(trackId)
            Timber.d("[MAIN_VM] Successfully bookmarked track %s", trackId)
        } catch (e: Exception) {
            Timber.e(e, "[MAIN_VM] Failed to bookmark track %s", trackId)
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
        // Without a specific query, we just refresh community tracks as "vibes"
        fetchCommunityTracks()
    }
}
