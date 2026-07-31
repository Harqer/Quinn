package com.musically.studio.network

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import com.musically.studio.dataconnect.*

interface ApiClient {
    suspend fun bookmarkTrack(trackId: String): Boolean
    suspend fun likeTrack(trackId: String): Boolean
    suspend fun generateLyrics(trackId: String, audioUrl: String): String?
    suspend fun shareVibe(trackId: String): String?
    suspend fun addToPlaylist(trackId: String, playlistId: String? = null): Boolean
    suspend fun saveProfile(name: String, birthday: String, gender: String): Boolean
    suspend fun savePreferences(artists: List<String>): Boolean
    suspend fun getUserTracks(): List<MaveTrack>?
    suspend fun getVibesByUserId(userId: String): List<MaveTrack>?
    suspend fun reportTarget(targetId: String, targetType: String, reason: String): Boolean
    suspend fun getCommunityTracks(): List<MaveTrack>?
    suspend fun getLikedTracks(): List<MaveTrack>?

    suspend fun generateMusicPrompts(imageB64: String): List<String>?
    suspend fun generateMusicFromMedia(base64: String, mimeType: String): MaveTrack?
    suspend fun generateCoverMedia(prompt: String, type: String): String?
    suspend fun getTrack(trackId: String): MaveTrack?
    suspend fun getSpotifyStatus(): Boolean
    suspend fun verifyDigitalCredential(credentialJson: String, nonce: String): String?
    suspend fun getSpotifyPlaylists(): List<MavePlaylist>?
    
    suspend fun getPlaylists(): List<MavePlaylist>?
    suspend fun getCategories(): List<MaveCategory>?
    suspend fun getAlbums(): List<MaveAlbum>?
    suspend fun getPodcasts(): List<MavePodcast>?
    suspend fun getAudiobooks(): List<MaveAudiobook>?
    suspend fun getSpotifyLibraryTracks(): List<MaveTrack>?
    suspend fun deleteAccount(): Boolean
    suspend fun createStripeCheckoutSession(returnUrl: String): String?
    suspend fun createStripePortalSession(returnUrl: String): String?
}

class RealApiClient(private val client: OkHttpClient) : ApiClient {
    private val BASE_URL = com.musically.studio.shared.BuildConfig.API_BASE_URL
    private val JSON = "application/json; charset=utf-8".toMediaType()

    override suspend fun bookmarkTrack(trackId: String): Boolean {
        return sendInteraction("BOOKMARK", trackId, "TRACK")
    }

    override suspend fun likeTrack(trackId: String): Boolean {
        return sendInteraction("LIKE", trackId, "TRACK")
    }

    private suspend fun sendInteraction(type: String, entityId: String, entityType: String): Boolean {
        val token = TokenManager.getValidToken() ?: return false
        val json = JSONObject().apply {
            put("type", type)
            put("entityId", entityId)
            put("entityType", entityType)
        }
        val body = json.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url("$BASE_URL/api/interactions")
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

    override suspend fun generateLyrics(trackId: String, audioUrl: String): String? {
        val token = TokenManager.getValidToken() ?: return null
        val json = JSONObject().apply {
            put("trackId", trackId)
            put("audioUrl", audioUrl)
        }
        val body = json.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url("$BASE_URL/music/lyrics")
            .header("Authorization", "Bearer $token")
            .post(body)
            .build()
        
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val respJson = JSONObject(response.body?.string() ?: "{}")
                    respJson.optString("lyrics")
                } else null
            }
        } catch (e: Exception) { null }
    }

    override suspend fun generateMusicFromMedia(base64: String, mimeType: String): MaveTrack? {
        val token = TokenManager.getValidToken() ?: return null
        val json = JSONObject().apply {
            put("data", base64)
            put("mimeType", mimeType)
        }
        val body = json.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url("$BASE_URL/music/generate-from-media")
            .header("Authorization", "Bearer $token")
            .post(body)
            .build()
        
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseJson = JSONObject(response.body?.string() ?: "{}")
                    val nameStr = responseJson.optString("title", responseJson.optString("trackName", "Media Inspired Track"))
                    val artistStr = responseJson.optString("artist", responseJson.optString("artistName", "Mave"))
                    val coverStr = responseJson.optString("coverUrl", "")
                    MaveTrack(
                        id = responseJson.optString("id", responseJson.optString("trackId", UUID.randomUUID().toString())),
                        name = nameStr,
                        artists = listOf(MaveArtist(id = UUID.nameUUIDFromBytes(artistStr.toByteArray()).toString(), name = artistStr)),
                        album = MaveAlbum(
                            id = UUID.nameUUIDFromBytes("Generated".toByteArray()).toString(),
                            name = "Generated",
                            images = listOfNotNull(coverStr.takeIf { it.isNotEmpty() }?.let { MaveImage(url = it) })
                        ),
                        audioUrl = responseJson.optString("url", responseJson.optString("audioUrl", ""))
                    )
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

    override suspend fun deleteAccount(): Boolean {
        val token = TokenManager.getValidToken() ?: return false
        val request = Request.Builder()
            .url("$BASE_URL/auth/delete")
            .header("Authorization", "Bearer $token")
            .delete()
            .build()
        return executeRequest(request)
    }

    override suspend fun verifyDigitalCredential(credentialJson: String, nonce: String): String? {
        val body = """{"credential": "$credentialJson", "nonce": "$nonce"}""".toRequestBody(JSON)
        val request = Request.Builder()
            .url("$BASE_URL/auth/verifyDigitalCredential")
            .post(body)
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val resBody = response.body?.string() ?: return null
                    JSONObject(resBody).getString("customToken")
                } else null
            }
        } catch (e: Exception) {
            null
        }
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
            .url("$BASE_URL/api/user/tracks")
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
                    } else emptyList()
                } else null
            }
        } catch (e: Exception) { null }
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
            .url("$BASE_URL/api/community/tracks")
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
                    } else emptyList()
                } else null
            }
        } catch (e: Exception) { null }
    }

    override suspend fun getLikedTracks(): List<MaveTrack>? {
        val token = TokenManager.getValidToken() ?: return null
        val request = Request.Builder()
            .url("$BASE_URL/api/interactions/liked")
            .header("Authorization", "Bearer $token")
            .get()
            .build()
        
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrEmpty()) {
                        val gson = com.google.gson.Gson()
                        val type = object : com.google.gson.reflect.TypeToken<List<MaveTrack>>() {}.type
                        gson.fromJson<List<MaveTrack>>(body, type)
                    } else emptyList()
                } else null
            }
        } catch (e: IOException) {
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

    override suspend fun generateMusicPrompts(imageB64: String): List<String>? {
        val token = TokenManager.getValidToken() ?: return null
        val json = JSONObject().apply {
            put("image", imageB64)
        }
        val body = json.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url("$BASE_URL/generate")
            .header("Authorization", "Bearer $token")
            .post(body)
            .build()
        
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val respBody = response.body?.string()
                    if (!respBody.isNullOrEmpty()) {
                        val respJson = JSONObject(respBody)
                        val promptsArray = respJson.optJSONArray("prompts")
                        val list = mutableListOf<String>()
                        if (promptsArray != null) {
                            for (i in 0 until promptsArray.length()) {
                                list.add(promptsArray.getString(i))
                            }
                        }
                        list
                    } else emptyList()
                } else null
            }
        } catch (e: Exception) {
            Timber.e(e, "Error generating music prompts")
            null
        }
    }

    override suspend fun generateCoverMedia(prompt: String, type: String): String? {
        val token = TokenManager.getValidToken() ?: return null
        val json = JSONObject().apply {
            put("image", prompt)
            put("type", type)
        }
        val body = json.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url("$BASE_URL/generate")
            .header("Authorization", "Bearer $token")
            .post(body)
            .build()
        
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val respBody = response.body?.string()
                    if (!respBody.isNullOrEmpty()) {
                        val respJson = JSONObject(respBody)
                        respJson.optString("url")
                    } else null
                } else null
            }
        } catch (e: Exception) {
            Timber.e(e, "Error generating cover media")
            null
        }
    }

    override suspend fun getTrack(trackId: String): MaveTrack? {
        val request = Request.Builder()
            .url("$BASE_URL/tracks/$trackId")
            .get()
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrEmpty()) {
                        val gson = com.google.gson.Gson()
                        gson.fromJson(body, MaveTrack::class.java)
                    } else null
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getSpotifyStatus(): Boolean {
        val token = TokenManager.getValidToken() ?: return false
        val request = Request.Builder()
            .url("$BASE_URL/spotify/status")
            .header("Authorization", "Bearer $token")
            .get()
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val respJson = JSONObject(response.body?.string() ?: "{}")
                    respJson.optBoolean("connected", false)
                } else false
            }
        } catch (e: Exception) { false }
    }

    override suspend fun getSpotifyPlaylists(): List<MavePlaylist>? {
        val token = TokenManager.getValidToken() ?: return null
        val request = Request.Builder()
            .url("$BASE_URL/spotify/playlists")
            .header("Authorization", "Bearer $token")
            .get()
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrEmpty()) {
                        val json = JSONObject(body)
                        val items = json.optJSONArray("items")
                        val list = mutableListOf<MavePlaylist>()
                        if (items != null) {
                            for (i in 0 until items.length()) {
                                val item = items.getJSONObject(i)
                                val images = item.optJSONArray("images")
                                val coverUrl = if (images != null && images.length() > 0) images.getJSONObject(0).optString("url") else ""
                                list.add(MavePlaylist(
                                    id = item.optString("id"),
                                    name = item.optString("name"),
                                    coverUrl = coverUrl
                                ))
                            }
                        }
                        list
                    } else null
                } else null
            }
        } catch (e: Exception) { null }
    }

    override suspend fun getPlaylists(): List<MavePlaylist>? {
        val request = Request.Builder()
            .url("$BASE_URL/api/playlists")
            .get()
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrEmpty()) {
                        val gson = com.google.gson.Gson()
                        val listType = object : com.google.gson.reflect.TypeToken<List<MavePlaylist>>() {}.type
                        gson.fromJson(body, listType)
                    } else emptyList()
                } else null
            }
        } catch (e: Exception) { null }
    }

    override suspend fun getCategories(): List<MaveCategory>? {
        val request = Request.Builder()
            .url("$BASE_URL/api/categories")
            .get()
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrEmpty()) {
                        val gson = com.google.gson.Gson()
                        val listType = object : com.google.gson.reflect.TypeToken<List<MaveCategory>>() {}.type
                        gson.fromJson(body, listType)
                    } else emptyList()
                } else null
            }
        } catch (e: Exception) { null }
    }

    override suspend fun getAlbums(): List<MaveAlbum>? {
        val request = Request.Builder()
            .url("$BASE_URL/api/albums")
            .get()
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrEmpty()) {
                        val gson = com.google.gson.Gson()
                        val listType = object : com.google.gson.reflect.TypeToken<List<MaveAlbum>>() {}.type
                        gson.fromJson(body, listType)
                    } else emptyList()
                } else null
            }
        } catch (e: Exception) { null }
    }

    override suspend fun getPodcasts(): List<MavePodcast>? {
        val request = Request.Builder()
            .url("$BASE_URL/api/podcasts")
            .get()
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrEmpty()) {
                        val gson = com.google.gson.Gson()
                        val listType = object : com.google.gson.reflect.TypeToken<List<MavePodcast>>() {}.type
                        gson.fromJson(body, listType)
                    } else emptyList()
                } else null
            }
        } catch (e: Exception) { null }
    }

    override suspend fun getAudiobooks(): List<MaveAudiobook>? {
        val request = Request.Builder()
            .url("$BASE_URL/api/audiobooks")
            .get()
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrEmpty()) {
                        val gson = com.google.gson.Gson()
                        val listType = object : com.google.gson.reflect.TypeToken<List<MaveAudiobook>>() {}.type
                        gson.fromJson(body, listType)
                    } else emptyList()
                } else null
            }
        } catch (e: Exception) { null }
    }

    override suspend fun getSpotifyLibraryTracks(): List<MaveTrack>? {
        val token = TokenManager.getValidToken() ?: return null
        val request = Request.Builder()
            .url("$BASE_URL/spotify/library")
            .header("Authorization", "Bearer $token")
            .get()
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrEmpty()) {
                        val json = JSONObject(body)
                        val items = json.optJSONArray("items")
                        val list = mutableListOf<MaveTrack>()
                        if (items != null) {
                            for (i in 0 until items.length()) {
                                val item = items.getJSONObject(i)
                                val album = item.optJSONObject("album")
                                val images = album?.optJSONArray("images")
                                val coverUrl = if (images != null && images.length() > 0) images.getJSONObject(0).optString("url") else ""
                                val artists = item.optJSONArray("artists")
                                val artistName = if (artists != null && artists.length() > 0) artists.getJSONObject(0).optString("name") else "Unknown Artist"
                                val previewUrl = item.optString("preview_url")
                                
                                list.add(MaveTrack(
                                    id = item.optString("id"),
                                    name = item.optString("name"),
                                    artists = listOf(MaveArtist(id = UUID.nameUUIDFromBytes(artistName.toByteArray()).toString(), name = artistName)),
                                    album = MaveAlbum(
                                        id = UUID.nameUUIDFromBytes((album?.optString("name") ?: "Unknown").toByteArray()).toString(),
                                        name = album?.optString("name") ?: "Unknown",
                                        images = listOfNotNull(coverUrl.takeIf { it.isNotEmpty() }?.let { MaveImage(url = it) })
                                    ),
                                    durationMs = item.optInt("duration_ms", 0).toLong() * 1000
                                ))
                            }
                        }
                        list
                    } else null
                } else null
            }
        } catch (e: Exception) { null }
    }

    override suspend fun createStripeCheckoutSession(returnUrl: String): String? {
        val token = TokenManager.getValidToken() ?: return null
        val json = JSONObject().apply {
            put("returnUrl", returnUrl)
        }
        val body = json.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url("$BASE_URL/stripe/checkout-session")
            .header("Authorization", "Bearer $token")
            .post(body)
            .build()
        
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val respBody = response.body?.string()
                    if (!respBody.isNullOrEmpty()) {
                        val respJson = JSONObject(respBody)
                        respJson.optString("url")
                    } else null
                } else null
            }
        } catch (e: Exception) {
            Timber.e(e, "Error creating checkout session")
            null
        }
    }

    override suspend fun createStripePortalSession(returnUrl: String): String? {
        val token = TokenManager.getValidToken() ?: return null
        val json = JSONObject().apply {
            put("returnUrl", returnUrl)
        }
        val body = json.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url("$BASE_URL/stripe/portal-session")
            .header("Authorization", "Bearer $token")
            .post(body)
            .build()
        
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val respBody = response.body?.string()
                    if (!respBody.isNullOrEmpty()) {
                        val respJson = JSONObject(respBody)
                        respJson.optString("url")
                    } else null
                } else null
            }
        } catch (e: Exception) {
            Timber.e(e, "Error creating portal session")
            null
        }
    }
}
