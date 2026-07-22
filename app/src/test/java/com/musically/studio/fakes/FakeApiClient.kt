package com.musically.studio.fakes

import com.musically.studio.network.ApiClient
import com.musically.studio.network.MaveTrack

class FakeApiClient : ApiClient {
    var bookmarkResult = true
    var shareResult: String? = "https://mave.studio/share/123"
    var saveProfileResult = true
    var savePreferencesResult = true
    var userTracksResult: List<MaveTrack>? = emptyList()
    var communityTracksResult: List<MaveTrack>? = emptyList()
    var verifyCredentialResult: String? = "fake_custom_token"

    override suspend fun bookmarkTrack(trackId: String): Boolean = bookmarkResult
    override suspend fun shareVibe(trackId: String): String? = shareResult
    override suspend fun addToPlaylist(trackId: String, playlistId: String?): Boolean = true
    override suspend fun saveProfile(name: String, birthday: String, gender: String): Boolean = saveProfileResult
    override suspend fun savePreferences(artists: List<String>): Boolean = savePreferencesResult
    override suspend fun getUserTracks(): List<MaveTrack>? = userTracksResult
    override suspend fun getVibesByUserId(userId: String): List<MaveTrack>? = userTracksResult
    override suspend fun reportTarget(targetId: String, targetType: String, reason: String): Boolean = true
    override suspend fun getCommunityTracks(): List<MaveTrack>? = communityTracksResult
    override suspend fun verifyDigitalCredential(credentialJson: String, nonce: String): String? = verifyCredentialResult
}
