package com.musically.studio.network

class FakeApiClient : ApiClient {
    override suspend fun getCommunityTracks(): List<MaveTrack>? = listOf(
        MaveTrack(
            id = "1",
            name = "Test Track",
            artists = emptyList(),
            album = MaveAlbum(id = "1", name = "Test Album", images = emptyList())
        )
    )
    override suspend fun getSpotifyLibraryTracks(): List<MaveTrack>? = emptyList()
    override suspend fun bookmarkTrack(trackId: String): Boolean = true
    override suspend fun shareVibe(trackId: String): String? = null
    override suspend fun addToPlaylist(trackId: String, playlistId: String?): Boolean = true
    override suspend fun saveProfile(name: String, birthday: String, gender: String): Boolean = true
    override suspend fun savePreferences(artists: List<String>): Boolean = true
    override suspend fun getUserTracks(): List<MaveTrack>? = emptyList()
    override suspend fun getVibesByUserId(userId: String): List<MaveTrack>? = emptyList()
    override suspend fun reportTarget(targetId: String, targetType: String, reason: String): Boolean = true
    override suspend fun generateMusicPrompts(imageB64: String): List<String>? = emptyList()
    override suspend fun generateCoverMedia(prompt: String, type: String): String? = null
    override suspend fun getTrack(trackId: String): MaveTrack? = null
    override suspend fun getSpotifyStatus(): Boolean = true
    override suspend fun verifyDigitalCredential(credentialJson: String, nonce: String): String? = null
    override suspend fun getSpotifyPlaylists(): List<MavePlaylist>? = emptyList()
    override suspend fun getPlaylists(): List<MavePlaylist>? = emptyList()
    override suspend fun getCategories(): List<MaveCategory>? = emptyList()
    override suspend fun getAlbums(): List<MaveAlbum>? = emptyList()
    override suspend fun getPodcasts(): List<MavePodcast>? = emptyList()
    override suspend fun getAudiobooks(): List<MaveAudiobook>? = emptyList()
    override suspend fun deleteAccount(): Boolean = true
    override suspend fun likeTrack(trackId: String): Boolean = true
    override suspend fun generateLyrics(trackId: String, audioUrl: String): String? = "Lyrics"
    override suspend fun generateMusicFromMedia(base64: String, mimeType: String): MaveTrack? = null
    override suspend fun createStripeCheckoutSession(returnUrl: String): String? = "checkout_session"
    override suspend fun createStripePortalSession(returnUrl: String): String? = "portal_session"
}
