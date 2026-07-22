package com.musically.studio.network

class FakeApiClient : ApiClient {
    override suspend fun bookmarkTrack(trackId: String): Boolean = true
    override suspend fun shareVibe(trackId: String): String? = "https://mave.com/v/$trackId"
    override suspend fun addToPlaylist(trackId: String, playlistId: String?): Boolean = true
    override suspend fun saveProfile(name: String, birthday: String, gender: String): Boolean = true
    override suspend fun savePreferences(artists: List<String>): Boolean = true
    override suspend fun getUserTracks(): List<MaveTrack>? = emptyList()
    override suspend fun getVibesByUserId(userId: String): List<MaveTrack>? = emptyList()
    override suspend fun reportTarget(targetId: String, targetType: String, reason: String): Boolean = true
    override suspend fun getCommunityTracks(): List<MaveTrack>? = listOf(
        MaveTrack(
            id = "1",
            name = "Test Track",
            artists = listOf(MaveArtist(id = "a1", name = "Test Artist")),
            album = MaveAlbum(id = "al1", name = "Test Album", images = emptyList()),
            userId = "u1"
        )
    )
    override suspend fun verifyDigitalCredential(credentialJson: String, nonce: String): String? = "mock-token"
    override suspend fun generateMusicPrompts(imageB64: String): List<String>? = emptyList()
}
