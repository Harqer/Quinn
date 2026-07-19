package com.musically.studio.network

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException

class ApiClient(private val client: OkHttpClient) {
    private val BASE_URL = "https://musically-studio.run.app/api"
    private val JSON = "application/json; charset=utf-8".toMediaType()

    suspend fun bookmarkTrack(trackId: String): Boolean {
        val token = TokenManager.getValidToken() ?: return false
        val body = """{"trackId": "$trackId"}""".toRequestBody(JSON)
        val request = Request.Builder()
            .url("$BASE_URL/music/bookmark")
            .header("Authorization", "Bearer $token")
            .post(body)
            .build()

        return executeRequest(request)
    }

    suspend fun getUserTracks(): List<SpotifyTrack>? {
        val token = TokenManager.getValidToken() ?: return null
        val request = Request.Builder()
            .url("$BASE_URL/music/user/tracks")
            .header("Authorization", "Bearer $token")
            .get()
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    // Parse SpotifyTrack list from JSON
                    emptyList() // Placeholder for actual parsing
                } else {
                    null
                }
            }
        } catch (e: IOException) {
            null
        }
    }

    suspend fun reportTarget(targetId: String, targetType: String, reason: String): Boolean {
        val token = TokenManager.getValidToken() ?: return false
        val body = """{
            "targetId": "$targetId",
            "targetType": "$targetType",
            "reason": "$reason"
        }""".trimIndent().toRequestBody(JSON)
        val request = Request.Builder()
            .url("$BASE_URL/reports")
            .header("Authorization", "Bearer $token")
            .post(body)
            .build()

        return executeRequest(request)
    }

    private fun executeRequest(request: Request): Boolean {
        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Timber.e("API Error: ${response.code} ${response.message}")
                }
                response.isSuccessful
            }
        } catch (e: IOException) {
            Timber.e(e, "Network Error")
            false
        }
    }
}
