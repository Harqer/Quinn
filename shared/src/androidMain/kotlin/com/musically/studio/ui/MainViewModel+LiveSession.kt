/**
 * @AtomicLevel: Template/Page
 * @SemanticPurpose: Android Component for MainViewModel+LiveSession.kt
 */

package com.musically.studio.ui

import androidx.lifecycle.viewModelScope
import com.musically.studio.ui.models.ChatMessage
import com.musically.studio.network.MaveTrack
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlinx.coroutines.tasks.await

fun MainViewModel.startLiveSession() {
    viewModelScope.launch {
        _isLiveSessionActive.value = true
        try {
            geminiLiveManager.connect()
        } catch (e: Exception) {
            Timber.e(e, "Error starting live session")
        }
    }
}

fun MainViewModel.stopLiveSession() {
    geminiLiveManager.disconnect()
    _isLiveSessionActive.value = false
}

fun MainViewModel.sendLiveSessionMessage(text: String) {
    viewModelScope.launch {
        try {
            geminiLiveManager.sendText(text)
        } catch (e: Exception) {
            Timber.e(e, "Error sending text to Gemini")
        }
    }
}

fun MainViewModel.sendVisionFrame(bytes: ByteArray) {
    viewModelScope.launch {
        try {
            geminiLiveManager.sendVideoFrame(bytes)
        } catch (e: Exception) {
            Timber.e(e, "Error sending video frame")
        }
    }
}

fun MainViewModel.generateFromTrack(track: MaveTrack) {
    sendTextCommand("Generate a track similar to ${track.name} by ${track.album?.artists?.firstOrNull()?.name ?: "Unknown Artist"}")
}
