/**
 * @AtomicLevel: Template/Page
 * @SemanticPurpose: Android Component for MainViewModel+Interaction.kt
 */

package com.musically.studio.ui

import androidx.lifecycle.viewModelScope
import com.musically.studio.network.MaveTrack
import kotlinx.coroutines.launch
import timber.log.Timber
import android.content.Intent

fun MainViewModel.likeTrack(trackId: String) {
    viewModelScope.launch {
        if (dataConnectRepository.likeTrack(trackId)) {
            val track = _tracks.value.find { it.id == trackId } 
                ?: _communityTracks.value.find { it.id == trackId }
                ?: _recentTracks.value.find { it.id == trackId }
            if (track != null && _likedTracks.value.none { it.id == trackId }) {
                _likedTracks.value = _likedTracks.value + track
            }
        }
    }
}
fun MainViewModel.unlikeTrack(trackId: String) {
    viewModelScope.launch {
        if (dataConnectRepository.unlikeTrack(trackId)) {
            // Optimistic UI updates
            _likedTracks.value = _likedTracks.value.filter { it.id != trackId }
            _bookmarkedTracks.value = _bookmarkedTracks.value.filter { it.id != trackId }
        }
    }
}
fun MainViewModel.addTrackToPlaylist(trackId: String, playlistId: String) {
    viewModelScope.launch {
        if (dataConnectRepository.addTrackToPlaylist(trackId, playlistId)) {
            Timber.d("Added track $trackId to playlist $playlistId")
        }
    }
}
fun MainViewModel.bookmarkTrack(trackId: String) {
    viewModelScope.launch {
        if (dataConnectRepository.bookmarkTrack(trackId)) {
            val track = _tracks.value.find { it.id == trackId } 
                ?: _communityTracks.value.find { it.id == trackId }
                ?: _recentTracks.value.find { it.id == trackId }
            if (track != null && _bookmarkedTracks.value.none { it.id == trackId }) {
                _bookmarkedTracks.value = _bookmarkedTracks.value + track
            }
        }
    }
}
fun MainViewModel.shareTrack(trackId: String, callback: ((String?) -> Unit)? = null) {
    val url = "https://lyria.studio/track/$trackId"
    callback?.invoke(url)
}
fun MainViewModel.recordPlay(trackId: String) {
    viewModelScope.launch {
        Timber.d("Recorded play for track $trackId")
        val track = _tracks.value.find { it.id == trackId } 
            ?: _communityTracks.value.find { it.id == trackId }
            ?: _likedTracks.value.find { it.id == trackId }
            ?: _bookmarkedTracks.value.find { it.id == trackId }
        
        if (track != null) {
            val currentRecents = _recentTracks.value.toMutableList()
            currentRecents.removeAll { it.id == trackId }
            currentRecents.add(0, track)
            _recentTracks.value = currentRecents.take(50) // Keep last 50
        }
        // Persist to DataConnect for cross-device history and recommendation signals.
        try {
            dataConnectRepository.recordPlay(trackId)
        } catch (e: Exception) {
            // Non-fatal: local state is already updated; log but don't surface to user.
            Timber.e(e, "Failed to persist play record for track $trackId to DataConnect")
        }
    }
}
fun MainViewModel.downloadTrack(trackId: String, context: android.content.Context) {
    viewModelScope.launch {
        val track = _tracks.value.find { it.id == trackId } 
            ?: _communityTracks.value.find { it.id == trackId }
            ?: _recentTracks.value.find { it.id == trackId }
            ?: _searchResults.value.find { it.id == trackId }
        
        if (track != null && !track.audioUrl.isNullOrEmpty()) {
            val downloadManager = context.getSystemService(android.content.Context.DOWNLOAD_SERVICE) as android.app.DownloadManager
            val request = android.app.DownloadManager.Request(android.net.Uri.parse(track.audioUrl))
                .setTitle(track.name)
                .setDescription("Downloading ${track.name}...")
                .setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalFilesDir(context, android.os.Environment.DIRECTORY_MUSIC, "${track.id}.mp3")
            
            try {
                downloadManager.enqueue(request)
                
                val json = prefs.getString("downloaded_tracks", "[]")
                val type = object : com.google.gson.reflect.TypeToken<List<MaveTrack>>() {}.type
                val gson = com.google.gson.Gson()
                val currentDownloads: MutableList<MaveTrack> = gson.fromJson(json, type) ?: mutableListOf()
                if (currentDownloads.none { it.id == trackId }) {
                    currentDownloads.add(track)
                    prefs.edit().putString("downloaded_tracks", gson.toJson(currentDownloads)).apply()
                    _downloadedTracks.value = currentDownloads
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to enqueue download for track ${track.id}")
            }
        }
    }
}
