package com.musically.studio.network.fakes

import com.musically.studio.network.MaveSessionManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.OkHttpClient

class FakeMaveSessionManager(
    client: OkHttpClient = OkHttpClient()
) : MaveSessionManager(client) {

    private val _fakeEvents = MutableSharedFlow<String>(extraBufferCapacity = 10)
    override val events = _fakeEvents.asSharedFlow()

    private val _fakeAudioStream = MutableSharedFlow<ByteArray>(extraBufferCapacity = 100)
    override val audioStream = _fakeAudioStream.asSharedFlow()

    val sentEvents = mutableListOf<Pair<String, Map<String, Any>>>()
    val sentAudio = mutableListOf<String>()
    val sentVideoFrames = mutableListOf<ByteArray>()
    val sentPrompts = mutableListOf<List<Map<String, Any>>>()
    var isConnected = false
    var playbackState: String? = null

    override fun connect() {
        isConnected = true
    }

    override fun disconnect() {
        isConnected = false
    }

    override fun sendEvent(type: String, data: Map<String, Any>) {
        sentEvents.add(type to data)
    }

    override fun sendPrompts(prompts: List<Map<String, Any>>) {
        sentPrompts.add(prompts)
    }

    override fun play(): Boolean? {
        playbackState = "play"
        return true
    }

    override fun pause(): Boolean? {
        playbackState = "pause"
        return true
    }

    override fun stop(): Boolean? {
        playbackState = "stop"
        return true
    }

    override fun next(): Boolean? {
        playbackState = "next"
        return true
    }

    override fun previous(): Boolean? {
        playbackState = "previous"
        return true
    }

    override fun sendAudio(base64: String) {
        sentAudio.add(base64)
    }

    override fun sendVideoFrame(frameBytes: ByteArray) {
        sentVideoFrames.add(frameBytes)
    }

    suspend fun emitFakeEvent(jsonEvent: String) {
        _fakeEvents.emit(jsonEvent)
    }

    suspend fun emitFakeAudio(bytes: ByteArray) {
        _fakeAudioStream.emit(bytes)
    }
}
