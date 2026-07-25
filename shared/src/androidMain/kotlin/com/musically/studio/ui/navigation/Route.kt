package com.musically.studio.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    @Serializable data object Login : Route
    @Serializable data object Home : Route
    @Serializable data object Discover : Route
    @Serializable data object Search : Route
    @Serializable data object Library : Route
    @Serializable data object Podcast : Route
    @Serializable data object Devices : Route
    @Serializable data class AlbumView(val albumId: String) : Route
    @Serializable data class UserProfile(val userId: String) : Route
    @Serializable data object Camera : Route
    @Serializable data object LiveSession : Route
    @Serializable data object Gallery : Route
    @Serializable data class PlaylistView(val playlistId: String) : Route
    @Serializable data class CategoryView(val categoryId: String) : Route
    // Onboarding Sequence
    @Serializable data object Welcome : Route
    @Serializable data object AuthOptions : Route
    @Serializable data object EmailInput : Route
    @Serializable data object PasswordInput : Route
    @Serializable data object BirthdayInput : Route
    @Serializable data object GenderInput : Route
    @Serializable data object NameTerms : Route
    @Serializable data object Loading : Route
    @Serializable data object Notification : Route
    @Serializable data object ArtistSelection : Route

    @Serializable data class NowPlaying(val trackId: String?) : Route
    @Serializable data class GenerateCover(val trackId: String?, val initialType: String = "image") : Route
    @Serializable data class TrackOptions(val trackId: String) : Route
    @Serializable data object Queue : Route
    @Serializable data class Lyrics(val trackId: String?) : Route
    @Serializable data object LiveSessionOptions : Route
}

data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)
