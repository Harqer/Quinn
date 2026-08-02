package com.musically.studio.ui

import timber.log.Timber
import com.musically.studio.network.MaveTrack
import android.content.Context
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

fun MainViewModel.fetchPlaylists() {
    viewModelScope.launch {
        Timber.d("[MAIN_VM] Fetching user playlists via DataConnect")
        try {
            dataConnectRepository.getPlaylists()
        } catch (e: Exception) {
            Timber.e(e, "[MAIN_VM] Failed to fetch playlists")
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
        Timber.d("[MAIN_VM] Generating cover media with prompt: %s", customPrompt)
        onComplete(null)
    }
}

fun MainViewModel.fetchUserTracks() {
    viewModelScope.launch {
        Timber.d("[MAIN_VM] Fetching user tracks via DataConnect")
        try {
            dataConnectRepository.getUserTracks()
        } catch (e: Exception) {
            Timber.e(e, "[MAIN_VM] Failed to fetch user tracks")
        }
    }
}

fun MainViewModel.fetchLikedTracks() {
    viewModelScope.launch {
        Timber.d("[MAIN_VM] Fetching liked tracks")
        try {
            dataConnectRepository.getLikedTracks()
        } catch (e: Exception) {
            Timber.e(e, "[MAIN_VM] Failed to fetch liked tracks")
        }
    }
}

fun MainViewModel.clearCatalogError() {
    Timber.d("[MAIN_VM] Clearing catalog error state")
}

fun MainViewModel.clearLiveSessionHistory() {
    Timber.d("[MAIN_VM] Clearing live session history")
}

fun MainViewModel.generateLyrics(trackId: String, audioUrl: String?) {
    Timber.d("[MAIN_VM] Generating lyrics for track: %s", trackId)
}

fun MainViewModel.fetchCommunityTracks() {
    viewModelScope.launch {
        Timber.d("[MAIN_VM] Fetching community tracks")
        try {
            dataConnectRepository.getCommunityTracks()
        } catch (e: Exception) {
            Timber.e(e, "[MAIN_VM] Failed to fetch community tracks")
        }
    }
}

fun MainViewModel.recordVoice(context: Context?) {
    Timber.d("[MAIN_VM] Initiating voice recording session")
}

fun MainViewModel.generatePodcast(topic: String) {
    Timber.d("[MAIN_VM] Generating podcast for topic: %s", topic)
}

fun MainViewModel.addToPlaylist(trackId: String) {
    Timber.d("[MAIN_VM] Adding track %s to playlist", trackId)
}

fun MainViewModel.viewArtist(context: Context, track: MaveTrack) {
    Timber.d("[MAIN_VM] Viewing artist for track %s", track.name)
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
    }
}

fun MainViewModel.stopRecording() {
    Timber.d("[MAIN_VM] Stopping voice recording session")
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
}
