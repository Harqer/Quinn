package com.musically.studio.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Base64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import timber.log.Timber

class StreamAudioPlayer {
    private var audioTrack: AudioTrack? = null
    private val sampleRate = 24000
    private val channelConfig = AudioFormat.CHANNEL_OUT_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat)
    private val queue = Channel<ByteArray>(Channel.UNLIMITED)
    private var isPlaying = false

    fun start() {
        if (isPlaying) return
        isPlaying = true

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setChannelMask(channelConfig)
                    .setEncoding(audioFormat)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                for (chunk in queue) {
                    if (!isPlaying) break
                    audioTrack?.write(chunk, 0, chunk.size)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error playing audio chunk")
            }
        }
    }

    fun queueAudioChunk(pcmData: ByteArray) {
        if (!isPlaying) {
            start()
        }
        try {
            queue.trySend(pcmData)
        } catch (e: Exception) {
            Timber.e(e, "Failed to queue audio chunk")
        }
    }

    fun queueAudioChunk(base64Data: String) {
        if (!isPlaying) {
            start()
        }
        try {
            val decodedBytes = Base64.decode(base64Data, Base64.NO_WRAP)
            queue.trySend(decodedBytes)
        } catch (e: Exception) {
            Timber.e(e, "Failed to decode audio chunk")
        }
    }

    fun stop() {
        isPlaying = false
        queue.close()
        try {
            audioTrack?.let {
                it.stop()
                it.release()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error stopping AudioTrack")
        } finally {
            audioTrack = null
        }
    }
}
