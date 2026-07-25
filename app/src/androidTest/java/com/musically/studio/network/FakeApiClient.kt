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
    override suspend fun generateCoverMedia(prompt: String, type: String): String? = null
    override suspend fun getPlaylists(): List<com.musically.studio.network.MavePlaylist>? = emptyList()
    override suspend fun getCategories(): List<com.musically.studio.network.MaveCategory>? = emptyList()
    override suspend fun getAlbums(): List<com.musically.studio.network.MaveAlbum>? = emptyList()
    override suspend fun getPodcasts(): List<com.musically.studio.network.MavePodcast>? = emptyList()
    override suspend fun getAudiobooks(): List<com.musically.studio.network.MaveAudiobook>? = emptyList()
    override suspend fun getTrack(trackId: String): MaveTrack? = null
    override suspend fun getSpotifyStatus(): Boolean = true
    override suspend fun getSpotifyPlaylists(): List<com.musically.studio.network.MavePlaylist>? = emptyList()
}
