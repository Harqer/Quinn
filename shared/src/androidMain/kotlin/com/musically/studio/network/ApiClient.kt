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
    suspend fun shareVibe(trackId: String): String?
    suspend fun addToPlaylist(trackId: String, playlistId: String? = null): Boolean
    suspend fun saveProfile(name: String, birthday: String, gender: String): Boolean
    suspend fun savePreferences(artists: List<String>): Boolean
    suspend fun getUserTracks(): List<MaveTrack>?
    suspend fun getVibesByUserId(userId: String): List<MaveTrack>?
    suspend fun reportTarget(targetId: String, targetType: String, reason: String): Boolean
    suspend fun getCommunityTracks(): List<MaveTrack>?

    suspend fun generateMusicPrompts(imageB64: String): List<String>?
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
}

class RealApiClient(private val client: OkHttpClient) : ApiClient {
    private val BASE_URL = com.musically.studio.shared.BuildConfig.API_BASE_URL
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
        val res = DefaultConnector.instance.getUserTracks.execute()
        return res.data.tracks.map { t ->
            MaveTrack(
                id = t.id,
                name = t.name,
                artists = listOf(MaveArtist(id = UUID.nameUUIDFromBytes(t.artistName.toByteArray()).toString(), name = t.artistName)),
                album = MaveAlbum(id = UUID.nameUUIDFromBytes(t.albumName.toByteArray()).toString(), name = t.albumName, images = listOfNotNull(t.imageUrl?.let { MaveImage(url = it) })),
                durationMs = 210000L // 3:30 standard placeholder for unbound durations in prod
            )
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
        val res = DefaultConnector.instance.getCommunityTracks.execute()
        return res.data.tracks.map { t ->
            MaveTrack(
                id = t.id,
                name = t.name,
                artists = listOf(MaveArtist(id = t.owner.uid, name = t.artistName)),
                album = MaveAlbum(id = UUID.nameUUIDFromBytes(t.albumName.toByteArray()).toString(), name = t.albumName, images = listOfNotNull(t.imageUrl?.let { MaveImage(url = it) })),
                durationMs = 210000L
            )
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
        val res = DefaultConnector.instance.getPlaylists.execute()
        return res.data.playlists.map { p ->
            MavePlaylist(
                id = p.id,
                name = p.name,
                coverUrl = p.imageUrl,
                description = p.description
            )
        }
    }

    override suspend fun getCategories(): List<MaveCategory>? {
        val res = DefaultConnector.instance.getCategories.execute()
        return res.data.categories.map { c ->
            MaveCategory(
                id = c.id,
                name = c.name,
                imageUrl = c.imageUrl,
                colorHex = "#8B5CF6"
            )
        }
    }

    override suspend fun getAlbums(): List<MaveAlbum>? {
        val res = DefaultConnector.instance.getAlbums.execute()
        return res.data.albums.map { a ->
            MaveAlbum(
                id = a.id,
                name = a.name,
                artists = listOf(MaveArtist(id = UUID.nameUUIDFromBytes(a.artistName.toByteArray()).toString(), name = a.artistName)),
                images = listOfNotNull(a.imageUrl?.takeIf { it.isNotEmpty() }?.let { MaveImage(it) }),
                description = "Release Year: ${a.releaseYear ?: "Unknown"}"
            )
        }
    }

    override suspend fun getPodcasts(): List<MavePodcast>? {
        val res = DefaultConnector.instance.getPodcasts.execute()
        return res.data.podcasts.map { p ->
            MavePodcast(
                id = p.id,
                name = p.name,
                publisher = p.publisher,
                imageUrl = p.imageUrl,
                description = p.description
            )
        }
    }

    override suspend fun getAudiobooks(): List<MaveAudiobook>? {
        val res = DefaultConnector.instance.getAudiobooks.execute()
        return res.data.audiobooks.map { a ->
            MaveAudiobook(
                id = a.id,
                title = a.title,
                author = a.author,
                narrator = a.narrator,
                imageUrl = a.imageUrl,
                duration = a.duration,
                audioUrl = a.audioUrl
            )
        }
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
}
