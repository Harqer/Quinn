package com.musically.studio.network

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException

interface ApiClient {
    suspend fun bookmarkTrack(trackId: String): Boolean
    suspend fun shareVibe(trackId: String): String?
    suspend fun addToPlaylist(trackId: String, playlistId: String? = null): Boolean
    suspend fun saveProfile(name: String, birthday: String, gender: String): Boolean
    suspend fun savePreferences(artists: List<String>): Boolean
    suspend fun getUserTracks(): List<MaveTrack>?
    suspend fun getVibesByUserId(userId: String): List<MaveTrack>?
    suspend fun reportTarget(targetId: String, targetType: String, reason: String): Boolean
    suspend fun getCommunityTracks(): List<MaveTrack>?
    suspend fun verifyDigitalCredential(credentialJson: String, nonce: String): String?
}

class RealApiClient(private val client: OkHttpClient) : ApiClient {
    private val BASE_URL = "https://musically-studio.run.app/api"
    private val JSON = "application/json; charset=utf-8".toMediaType()

    override suspend fun bookmarkTrack(trackId: String): Boolean {
        val token = TokenManager.getValidToken() ?: return false
        val body = """{"trackId": "$trackId"}""".toRequestBody(JSON)
        val request = Request.Builder()
            .url("$BASE_URL/music/bookmark")
            .header("Authorization", "Bearer $token")
            .post(body)
            .build()

        return executeRequest(request)
    }

    override suspend fun shareVibe(trackId: String): String? {
        val token = TokenManager.getValidToken() ?: return null
        val body = """{"trackId": "$trackId"}""".toRequestBody(JSON)
        val request = Request.Builder()
            .url("$BASE_URL/music/share")
            .header("Authorization", "Bearer $token")
            .post(body)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val json = JSONObject(response.body?.string() ?: "{}")
                    json.optString("url")
                } else null
            }
        } catch (e: Exception) { null }
    }

    override suspend fun addToPlaylist(trackId: String, playlistId: String?): Boolean {
        val token = TokenManager.getValidToken() ?: return false
        val json = JSONObject().apply {
            put("trackId", trackId)
            put("playlistId", playlistId)
        }
        val body = json.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url("$BASE_URL/music/playlist/add")
            .header("Authorization", "Bearer $token")
            .post(body)
            .build()
        return executeRequest(request)
    }

    override suspend fun saveProfile(name: String, birthday: String, gender: String): Boolean {
        val token = TokenManager.getValidToken() ?: return false
        val json = JSONObject().apply {
            put("name", name)
            put("birthday", birthday)
            put("gender", gender)
        }
        val body = json.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url("$BASE_URL/auth/profile")
            .header("Authorization", "Bearer $token")
            .post(body)
            .build()
        return executeRequest(request)
    }

    override suspend fun savePreferences(artists: List<String>): Boolean {
        val token = TokenManager.getValidToken() ?: return false
        val gson = com.google.gson.Gson()
        val body = gson.toJson(mapOf("artists" to artists)).toRequestBody(JSON)
        
        val request = Request.Builder()
            .url("$BASE_URL/auth/preferences")
            .header("Authorization", "Bearer $token")
            .post(body)
            .build()
        return executeRequest(request)
    }

    override suspend fun getUserTracks(): List<MaveTrack>? {
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
                    if (!body.isNullOrEmpty()) {
                        val gson = com.google.gson.Gson()
                        val trackResponse = gson.fromJson(body, MaveTracksResponse::class.java)
                        trackResponse?.items?.map { it.track } ?: emptyList()
                    } else {
                        emptyList()
                    }
                } else {
                    null
                }
            }
        } catch (e: IOException) {
            null
        } catch (e: Exception) {
            Timber.e(e, "Error parsing tracks")
            null
        }
    }

    override suspend fun getVibesByUserId(userId: String): List<MaveTrack>? {
        val token = TokenManager.getValidToken() ?: return null
        val request = Request.Builder()
            .url("$BASE_URL/music/user/$userId/vibes")
            .header("Authorization", "Bearer $token")
            .get()
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrEmpty()) {
                        val gson = com.google.gson.Gson()
                        val trackResponse = gson.fromJson(body, MaveTracksResponse::class.java)
                        trackResponse?.items?.map { it.track } ?: emptyList()
                    } else {
                        emptyList()
                    }
                } else null
            }
        } catch (e: Exception) { null }
    }

    override suspend fun reportTarget(targetId: String, targetType: String, reason: String): Boolean {
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

    override suspend fun getCommunityTracks(): List<MaveTrack>? {
        val request = Request.Builder()
            .url("$BASE_URL/music/community/tracks")
            .get()
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrEmpty()) {
                        val gson = com.google.gson.Gson()
                        val json = JSONObject(body)
                        val tracksArray = json.optJSONArray("tracks")
                        val list = mutableListOf<MaveTrack>()
                        if (tracksArray != null) {
                            for (i in 0 until tracksArray.length()) {
                                list.add(gson.fromJson(tracksArray.getString(i), MaveTrack::class.java))
                            }
                        }
                        list
                    } else {
                        emptyList()
                    }
                } else null
            }
        } catch (e: Exception) { null }
    }

    override suspend fun verifyDigitalCredential(credentialJson: String, nonce: String): String? {
        val json = JSONObject().apply {
            put("credentialJson", credentialJson)
            put("nonce", nonce)
        }
        val body = json.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url("$BASE_URL/auth/verify-credential")
            .post(body)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val respJson = JSONObject(response.body?.string() ?: "{}")
                    respJson.optString("token")
                } else null
            }
        } catch (e: Exception) {
            Timber.e(e, "Credential verification failed")
            null
        }
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
