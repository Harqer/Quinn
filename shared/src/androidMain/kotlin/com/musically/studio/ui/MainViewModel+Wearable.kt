/**
 * @AtomicLevel: Template/Page
 * @SemanticPurpose: Android Component for MainViewModel+Wearable.kt
 */

package com.musically.studio.ui

import kotlinx.coroutines.flow.collectLatest

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.musically.studio.network.*
import com.musically.studio.data.repository.*
import com.meta.wearable.dat.core.types.RegistrationState
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.util.*
import android.util.Base64
import kotlinx.coroutines.Job
import com.musically.studio.WearableStreamingService
import com.musically.studio.ui.models.DeviceType
import com.musically.studio.ui.models.AudioDevice
import com.musically.studio.ui.models.ChatMessage
import kotlinx.coroutines.delay

    internal fun MainViewModel.setupWearableFrameStreaming() {
        wearableFrameJob = viewModelScope.launch {
            WearableStreamingService.cameraFrames.collect { frameBytes ->
                if (_isWearableFrameStreamingEnabled.value && _isLiveSessionActive.value) {
                    geminiLiveManager.sendVideoFrame(frameBytes)
                    delay(WEARABLE_FRAME_INTERVAL_MS)
                }
            }
        }
    }

    fun MainViewModel.toggleWearableFrameStreaming() {
        _isWearableFrameStreamingEnabled.value = !_isWearableFrameStreamingEnabled.value
    }

    internal fun MainViewModel.setupWearableCollector() {
        viewModelScope.launch {
            try {
                com.meta.wearable.dat.core.Wearables.devices.collect { datDevices ->
                    val wearableAudioDevices = datDevices.map { device ->
                        AudioDevice(
                            id = device.identifier,
                            name = "Meta Glasses",
                            subtitle = "Meta Wearable",
                            type = DeviceType.BLUETOOTH,
                            isCurrent = false
                        )
                    }
                    val current = _devices.value.filter { it.subtitle != "Meta Wearable" }
                    _devices.value = current + wearableAudioDevices
                }
            } catch (e: Exception) {
                Timber.e(e, "Wearables SDK not initialized or failed (expected in tests)")
            }
        }

        viewModelScope.launch {
            WearableStreamingService.isServiceActive.collectLatest { active ->
                _isWearableConnected.value = active
            }
        }

        viewModelScope.launch {
            WearableStreamingService.interactionEvents.collect { event ->
                Timber.d("Wearable Interaction: $event")
                when (event) {
                    "play_pause" -> togglePlayPause()
                    "next" -> skipNext()
                    "previous" -> skipPrevious()
                    "stop" -> stopPlayback()
                    "generate" -> {
                        sendTextCommand("Generate a new atmosphere")
                        // No thinking UI on glasses, handled by companion app
                        WearableStreamingService.clearUi()
                    }
                    "speak" -> recordVoice(null)
                }
            }
        }
        
        viewModelScope.launch {
            WearableStreamingService.audioFrames.collect { base64 ->
                // Also send to Gemini Live for bidirectional dialogue if connected
                val pcmData = Base64.decode(base64, Base64.NO_WRAP)
                geminiLiveManager.sendAudio(pcmData)
            }
        }
        
        viewModelScope.launch {
            WearableStreamingService.cameraFrames.collect { bytes ->
                // Send POV to Gemini Live for visual reasoning
                geminiLiveManager.sendVideoFrame(bytes)
            }
        }

        viewModelScope.launch {
            geminiLiveManager.functionCalls.collect { call ->
                val name = call.getString("name")
                val args = call.optJSONObject("args")
                val callId = call.getString("id")
                
                if (name == "generate_visual_media") {
                    val intent = args?.optString("intent") ?: "cover_art"
                    val pitch = args?.optString("creative_pitch") ?: ""
                    
                    // Notify our backend LangGraph to trigger visual production
                    sendTextCommand("Production Request: Generate $intent. Creative vision: $pitch")
                    
                    // Return success to Gemini Live so it can acknowledge in dialogue
                    val result = JSONObject().apply { put("status", "Initiated production sequence.") }
                    geminiLiveManager.sendResponse(callId, name, result)
                }
            }
        }

        viewModelScope.launch {
            geminiLiveManager.transcripts.collect { text ->
                messages.add(0, ChatMessage(text, false))
            }
        }

        viewModelScope.launch {
            geminiLiveManager.thoughts.collect { thought ->
                Timber.d("Art Director Reasoning: $thought")
                // Update Wearable HUD
                // Keep glasses UI minimal
                WearableStreamingService.clearUi()
            }
        }

        viewModelScope.launch {
            geminiLiveManager.connectionState.collect { connected ->
                if (!connected) {
                    Timber.w("Gemini Live Disconnected")
                }
            }
        }

        viewModelScope.launch {
            isPlaying.collectLatest { playing ->
                val track = currentPlayingTrack.value
                if (track != null) {
                    WearableStreamingService.updateUi(
                        songTitle = track.name,
                        coverArtUrl = currentCoverUrl.value,
                        isPlaying = playing
                    )
                }
            }
        }

        viewModelScope.launch {
            currentPlayingTrack.collectLatest { track ->
                if (track != null) {
                    WearableStreamingService.updateUi(
                        songTitle = track.name,
                        coverArtUrl = currentCoverUrl.value,
                        isPlaying = isPlaying.value
                    )
                } else {
                    WearableStreamingService.clearUi() // Clear
                }
            }
        }
    }

    fun MainViewModel.setWearableConnected(context: Context, connected: Boolean) {
        val intent = Intent(context, WearableStreamingService::class.java)
        if (connected) {
            if (com.meta.wearable.dat.core.Wearables.registrationState.value != RegistrationState.REGISTERED) {
                Timber.w("Cannot connect wearable: User not registered with Meta AI app.")
                return
            }
            context.startForegroundService(intent)
        } else {
            context.stopService(intent)
        }
    }

