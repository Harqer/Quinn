package com.musically.studio.network.fakes

import com.musically.studio.network.ApiClient
import com.musically.studio.network.MaveAlbum
import com.musically.studio.network.MaveAudiobook
import com.musically.studio.network.MaveCategory
import com.musically.studio.network.MavePlaylist
import com.musically.studio.network.MavePodcast
import com.musically.studio.network.MaveTrack

class FakeApiClient : ApiClient {
    var bookmarkTrackResult = true
    var shareVibeResult: String? = "https://mave.studio/share/123"
    var addToPlaylistResult = true
    var saveProfileResult = true
    var savePreferencesResult = true
    var userTracks: List<MaveTrack>? = emptyList()
    var vibesByUserId: List<MaveTrack>? = emptyList()
    var reportTargetResult = true
    var communityTracks: List<MaveTrack>? = emptyList()
    var verifyDigitalCredentialResult: String? = "fake_token"
    var generateMusicPromptsResult: List<String>? = listOf("Prompt 1")
    var generateCoverMediaResult: String? = "https://example.com/cover.jpg"
    var getTrackResult: MaveTrack? = null
    var getSpotifyStatusResult = false
    var getSpotifyPlaylistsResult: List<MavePlaylist>? = emptyList()
    var getPlaylistsResult: List<MavePlaylist>? = emptyList()
    var getCategoriesResult: List<MaveCategory>? = emptyList()
    var getAlbumsResult: List<MaveAlbum>? = emptyList()
    var getPodcastsResult: List<MavePodcast>? = emptyList()
    var getAudiobooksResult: List<MaveAudiobook>? = emptyList()
    var getSpotifyLibraryTracksResult: List<MaveTrack>? = emptyList()
    var deleteAccountResult = true

    override suspend fun bookmarkTrack(trackId: String) = bookmarkTrackResult
    override suspend fun shareVibe(trackId: String) = shareVibeResult
    override suspend fun addToPlaylist(trackId: String, playlistId: String?) = addToPlaylistResult
    override suspend fun saveProfile(name: String, birthday: String, gender: String) = saveProfileResult
    override suspend fun savePreferences(artists: List<String>) = savePreferencesResult
    override suspend fun getUserTracks() = userTracks
    override suspend fun getVibesByUserId(userId: String) = vibesByUserId
    override suspend fun reportTarget(targetId: String, targetType: String, reason: String) = reportTargetResult
    override suspend fun getCommunityTracks() = communityTracks
    override suspend fun verifyDigitalCredential(credentialJson: String, nonce: String) = verifyDigitalCredentialResult
    override suspend fun generateMusicPrompts(imageB64: String) = generateMusicPromptsResult
    override suspend fun generateCoverMedia(prompt: String, type: String) = generateCoverMediaResult
    override suspend fun getTrack(trackId: String) = getTrackResult
    override suspend fun getSpotifyStatus() = getSpotifyStatusResult
    override suspend fun getSpotifyPlaylists() = getSpotifyPlaylistsResult
    override suspend fun getPlaylists() = getPlaylistsResult
    override suspend fun getCategories() = getCategoriesResult
    override suspend fun getAlbums() = getAlbumsResult
    override suspend fun getPodcasts() = getPodcastsResult
    override suspend fun getAudiobooks() = getAudiobooksResult
    override suspend fun getSpotifyLibraryTracks() = getSpotifyLibraryTracksResult
    override suspend fun deleteAccount() = deleteAccountResult
}
