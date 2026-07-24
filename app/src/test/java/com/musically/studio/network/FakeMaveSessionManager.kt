package com.musically.studio.network

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.OkHttpClient

class FakeMaveSessionManager(client: OkHttpClient) : MaveSessionManager(client) {
    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 10)
    override val events = _events.asSharedFlow()

    private val _audioStream = MutableSharedFlow<ByteArray>(extraBufferCapacity = 100)
    override val audioStream = _audioStream.asSharedFlow()

    fun emitMockEvent(eventJson: String) {
        _events.tryEmit(eventJson)
    }

    fun emitMockAudio(audioBytes: ByteArray) {
        _audioStream.tryEmit(audioBytes)
    }
}
