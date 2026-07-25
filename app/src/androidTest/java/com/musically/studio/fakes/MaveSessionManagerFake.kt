package com.musically.studio.fakes

import com.musically.studio.network.MaveSessionManager
import okhttp3.OkHttpClient

class MaveSessionManagerFake(client: OkHttpClient = OkHttpClient()) : MaveSessionManager(client) {
    var isConnected = false

    override fun connect() {
        isConnected = true
    }

    override fun disconnect() {
        isConnected = false
    }

    override fun sendEvent(type: String, data: Map<String, Any>) {
        // No-op for tests
    }

    override fun sendPrompts(prompts: List<Map<String, Any>>) {
        // No-op for tests
    }

    override fun play() {}
    override fun pause() {}
    override fun stop() {}
    override fun next() {}
    override fun previous() {}

    override fun sendAudio(base64: String) {}
    override fun sendVideoFrame(frameBytes: ByteArray) {}
}
