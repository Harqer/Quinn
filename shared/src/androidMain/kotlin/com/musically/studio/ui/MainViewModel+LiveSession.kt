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
            // Call the Firebase Function we set up to get the token!
            val tokenResult = com.google.firebase.functions.FirebaseFunctions.getInstance().getHttpsCallable("getLiveToken").call().await()
            val token = (tokenResult.data as? Map<String, Any>)?.get("token") as? String
            if (token != null) {
                geminiLiveManager.connect(token)
            } else {
                Timber.e("Failed to parse token from Firebase Function")
            }
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
    viewModelScope.launch {
        Timber.e("generateFromTrack is deprecated without external backend")
    }
}
