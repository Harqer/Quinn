package com.musically.studio.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import okhttp3.Response
import org.json.JSONObject
import timber.log.Timber

data class StreamEvent(
    val text: String? = null,
    val audioBase64: String? = null,
    val isComplete: Boolean = false,
    val trackInfo: MaveTrack? = null
)

class StreamingApiClient(private val client: OkHttpClient) {
    private val BASE_URL = com.musically.studio.shared.BuildConfig.API_BASE_URL
    private val JSON = "application/json; charset=utf-8".toMediaType()
    
    fun generatePodcastStream(prompt: String, voice: String = "AOEDE"): Flow<StreamEvent> = callbackFlow {
        val token = TokenManager.getValidToken()
        val json = JSONObject().apply {
            put("prompt", prompt)
            put("voice", voice)
        }
        val requestBuilder = Request.Builder()
            .url("$BASE_URL/music/podcast/generate")
            .post(json.toString().toRequestBody(JSON))
            .header("Accept", "text/event-stream")
            
        if (token != null) {
            requestBuilder.header("Authorization", "Bearer $token")
        }
        
        val eventSourceFactory = EventSources.createFactory(client)
        val eventSource = eventSourceFactory.newEventSource(requestBuilder.build(), object : EventSourceListener() {
            override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                try {
                    val eventJson = JSONObject(data)
                    when (eventJson.optString("type")) {
                        "chunk" -> {
                            trySend(StreamEvent(text = eventJson.optString("text")))
                        }
                        "audio_chunk" -> {
                            trySend(StreamEvent(audioBase64 = eventJson.optString("data")))
                        }
                        "complete" -> {
                            val trackObj = eventJson.optJSONObject("track")
                            var track: MaveTrack? = null
                            if (trackObj != null) {
                                track = MaveTrack(
                                    id = trackObj.optString("id"),
                                    name = trackObj.optString("title"),
                                    artists = listOf(MaveArtist(id = "mave", name = trackObj.optString("artist", "Mave AI Studio"))),
                                    album = MaveAlbum(
                                        id = "album", 
                                        name = trackObj.optString("album", "Narratives"),
                                        images = listOfNotNull(trackObj.optString("coverUrl").takeIf { it.isNotEmpty() }?.let { MaveImage(it) })
                                    ),
                                    audioUrl = trackObj.optString("audioUrl")
                                )
                            }
                            trySend(StreamEvent(isComplete = true, trackInfo = track))
                        }
                        "error" -> {
                            close(Exception(eventJson.optString("error")))
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error parsing SSE event data")
                }
            }

            override fun onClosed(eventSource: EventSource) {
                close()
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                Timber.e(t, "SSE Stream failed")
                close(t ?: Exception("SSE Stream failed with code ${response?.code}"))
            }
        })
        
        awaitClose {
            eventSource.cancel()
        }
    }
}
