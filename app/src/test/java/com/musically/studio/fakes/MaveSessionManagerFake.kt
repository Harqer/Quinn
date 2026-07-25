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

    override fun play(): Boolean? = true
    override fun pause(): Boolean? = true
    override fun stop(): Boolean? = true
    override fun next(): Boolean? = true
    override fun previous(): Boolean? = true

    override fun sendAudio(base64: String) {}
    override fun sendVideoFrame(frameBytes: ByteArray) {}
}
