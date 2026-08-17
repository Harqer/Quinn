/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: Android Component for Route.kt
 */

package com.musically.studio.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    @Serializable data object Home : Route
    @Serializable data object Discover : Route
    @Serializable data object Concerts : Route

    @Serializable data object Search : Route
    @Serializable data object Chat : Route
    @Serializable data object Library : Route
    @Serializable data object Podcast : Route
    @Serializable data object Devices : Route
    @Serializable data class AlbumView(val albumId: String) : Route
    @Serializable data class UserProfile(val userId: String) : Route
    @Serializable data object Camera : Route
    @Serializable data object LiveSession : Route
    @Serializable data object Gallery : Route
    @Serializable data object JamLobby : Route
    @Serializable data object JamRemix : Route
    @Serializable data object JamTrivia : Route
    @Serializable data object JamSongPicker : Route
    @Serializable data class PlaylistPicker(val trackId: String) : Route
    @Serializable data class PlaylistView(val playlistId: String) : Route
    @Serializable data class CategoryView(val categoryId: String) : Route
    @Serializable data object Settings : Route
    @Serializable data object Premium : Route
    @Serializable data object Recents : Route
    @Serializable data object Downloaded : Route
    // Onboarding Sequence
    @Serializable data object Welcome : Route
    @Serializable data object SignIn : Route
    @Serializable data object MfaEnrollment : Route
    @Serializable data object MfaVerification : Route

    @Serializable data class NowPlaying(val trackId: String?) : Route
    @Serializable data class GenerateCover(val trackId: String?, val initialType: String = "image") : Route
    @Serializable data class TrackOptions(val trackId: String) : Route
    @Serializable data object Queue : Route
    @Serializable data class Lyrics(val trackId: String?) : Route
    @Serializable data object LiveSessionOptions : Route
    @Serializable data class UsageLimitSheet(val reasonName: String) : Route
    @Serializable data class GeneratingSong(val imageBase64: String) : Route
    @Serializable data object PodcastOnboarding : Route
    @Serializable data class PodcastGenerator(val prompt: String, val isAudiobook: Boolean = false) : Route
    @Serializable data object NotFound : Route
}

data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)
