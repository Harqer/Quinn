package com.musically.studio.network

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Base64
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiveAudioPlayer @Inject constructor() {

    private var audioTrack: AudioTrack? = null
    private val sampleRate = 48000
    private val channelConfig = AudioFormat.CHANNEL_OUT_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val minBufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    init {
        initAudioTrack()
    }

    private fun initAudioTrack() {
        try {
            audioTrack = AudioTrack(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build(),
                AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setChannelMask(channelConfig)
                    .setEncoding(audioFormat)
                    .build(),
                minBufferSize,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE
            )
            audioTrack?.play()
            Timber.d("AudioTrack initialized and playing")
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize AudioTrack")
        }
    }

    fun feedPcmData(base64Chunk: String) {
        if (base64Chunk.isBlank()) return
        try {
            val pcmData = Base64.decode(base64Chunk, Base64.DEFAULT)
            // If the chunk is a WAV file, we skip the 44-byte header
            // Since we encoded it with a 44-byte header in the backend, we skip it here.
            // A basic check to see if it's WAV: size > 44 and starts with 'RIFF'
            var offset = 0
            var length = pcmData.size
            if (pcmData.size > 44 && pcmData[0] == 'R'.code.toByte() && pcmData[1] == 'I'.code.toByte()) {
                offset = 44
                length -= 44
            }
            
            val written = audioTrack?.write(pcmData, offset, length)
            if (written ?: 0 < 0) {
                Timber.e("Error writing to AudioTrack: $written")
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to decode and play audio chunk")
        }
    }

    fun stop() {
        audioTrack?.stop()
        audioTrack?.flush()
    }

    fun release() {
        audioTrack?.release()
        audioTrack = null
    }
}
