#!/bin/bash
cat << 'INNER_EOF' > shared/src/androidMain/kotlin/com/musically/studio/ui/MainViewModel+Firebase.kt
package com.musically.studio.ui

import timber.log.Timber
import com.musically.studio.network.*
import android.content.Context
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.catch
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.functions
import com.google.firebase.Firebase
import kotlinx.coroutines.tasks.await

fun MainViewModel.fetchPlaylists() {
    viewModelScope.launch {
        Timber.d("[MAIN_VM] Fetching user playlists via DataConnect")
        dataConnectRepository.getPlaylists()
            .onStart { _isLoading.value = true }
            .catch { e -> 
                Timber.e(e, "[MAIN_VM] Failed to fetch playlists")
                _catalogErrorMessage.value = e.message
                _isLoading.value = false
            }
            .collectLatest { items ->
                val mapped = items.map {
                    MavePlaylist(
                        id = it.id,
                        name = it.name,
                        coverUrl = it.coverUrl,
                        description = it.description
                    )
                }
                _playlists.value = mapped
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
        Timber.d("[MAIN_VM] Generating cover media with prompt: %s", customPrompt)
        onComplete(null)
    }
}

fun MainViewModel.fetchUserTracks() {
    viewModelScope.launch {
        Timber.d("[MAIN_VM] Fetching user tracks via DataConnect")
        dataConnectRepository.getUserTracks()
            .onStart { _isLoading.value = true }
            .catch { e ->
                Timber.e(e, "[MAIN_VM] Failed to fetch user tracks")
                _catalogErrorMessage.value = e.message
                _isLoading.value = false
            }
            .collectLatest { items ->
                val maveTracks = items.map { item ->
                    MaveTrack(
                        id = item.id,
                        name = item.title,
                        artists = listOf(MaveArtist(id = item.album.primaryArtist.id, name = item.album.primaryArtist.name)),
                        album = MaveAlbum(
                            id = item.album.id,
                            name = item.album.title,
                            images = item.coverUrl?.let { listOf(MaveImage(url = it)) } ?: emptyList()
                        ),
                        audioUrl = item.audioUrl,
                        durationMs = 0L
                    )
                }
                _tracks.value = maveTracks
                _isLoading.value = false
            }
    }
}

fun MainViewModel.fetchLikedTracks() {
    viewModelScope.launch {
        Timber.d("[MAIN_VM] Fetching liked tracks")
        dataConnectRepository.getLikedTracks()
            .onStart { _isLoading.value = true }
            .catch { e ->
                Timber.e(e, "[MAIN_VM] Failed to fetch liked tracks")
                _catalogErrorMessage.value = e.message
                _isLoading.value = false
            }
            .collectLatest { items ->
                val maveTracks = items.map { it.track }.map { item ->
                    MaveTrack(
                        id = item.id,
                        name = item.title,
                        artists = listOf(MaveArtist(id = item.album.primaryArtist.id, name = item.album.primaryArtist.name)),
                        album = MaveAlbum(
                            id = item.album.id,
                            name = item.album.title,
                            images = item.coverUrl?.let { listOf(MaveImage(url = it)) } ?: emptyList()
                        ),
                        audioUrl = item.audioUrl,
                        durationMs = 0L
                    )
                }
                _likedTracks.value = maveTracks
                _isLoading.value = false
            }
    }
}

fun MainViewModel.clearCatalogError() {
    Timber.d("[MAIN_VM] Clearing catalog error state")
    _catalogErrorMessage.value = null
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
        dataConnectRepository.getCommunityTracks()
            .onStart { _isLoading.value = true }
            .catch { e ->
                Timber.e(e, "[MAIN_VM] Failed to fetch community tracks")
                _catalogErrorMessage.value = e.message
                _isLoading.value = false
            }
            .collectLatest { items ->
                val maveTracks = items.map { item ->
                    MaveTrack(
                        id = item.id,
                        name = item.title,
                        artists = listOf(MaveArtist(id = item.album.primaryArtist.id, name = item.album.primaryArtist.name)),
                        album = MaveAlbum(
                            id = item.album.id,
                            name = item.album.title,
                            images = item.coverUrl?.let { listOf(MaveImage(url = it)) } ?: emptyList()
                        ),
                        audioUrl = "", // Fallback as it might not be fetched in current schema
                        userId = item.owner.uid,
                        durationMs = 0L
                    )
                }
                _communityTracks.value = maveTracks
                _isLoading.value = false
            }
    }
}

fun MainViewModel.recordVoice(context: Context?) {
    Timber.d("[MAIN_VM] Initiating voice recording session")
}

fun MainViewModel.generatePodcast(topic: String) {
    viewModelScope.launch {
        Timber.d("[MAIN_VM] Generating podcast for topic: %s", topic)
        try {
            val result = Firebase.functions.getHttpsCallable("generatePodcastScript").call(mapOf("topic" to topic)).await()
            val data = result.data as? Map<*, *>
            val script = data?.get("script") as? String
            Timber.d("[MAIN_VM] Generated Podcast Script: %s", script)
            // In a full implementation, you would update the UI state with the script.
        } catch (e: Exception) {
            Timber.e(e, "[MAIN_VM] Failed to generate podcast via Firebase Functions")
        }
    }
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
        fetchUserTracks()
        fetchCommunityTracks()
        fetchPlaylists()
        fetchLikedTracks()
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
INNER_EOF
chmod +x update_firebase_vm.sh
./update_firebase_vm.sh
