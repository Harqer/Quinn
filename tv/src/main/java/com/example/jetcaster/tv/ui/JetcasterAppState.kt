package com.example.jetcaster.tv.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import com.musically.studio.ui.jetcaster.core.player.model.PlayerEpisode
import kotlinx.serialization.Serializable

// ---------------------------------------------------------------------------
// Type-safe NavKey destinations for the TV app.
// Each destination is @Serializable so the back stack survives config changes.
// ---------------------------------------------------------------------------

@Serializable
data object Discover : NavKey

@Serializable
data object Library : NavKey

@Serializable
data object Search : NavKey

@Serializable
data object Profile : NavKey

@Serializable
data object Settings : NavKey

@Serializable
data object Player : NavKey

/** Navigate to a specific podcast's detail screen by its URI. */
@Serializable
data class PodcastDetail(val podcastUri: String) : NavKey

/** Navigate to a specific episode's detail screen by its URI. */
@Serializable
data class EpisodeDetail(val episodeUri: String) : NavKey

// ---------------------------------------------------------------------------
// App-level navigation state
// ---------------------------------------------------------------------------

/** Top-level routes that each have their own back stack (shown in the side nav). */
val TOP_LEVEL_ROUTES: Set<NavKey> = setOf(Discover, Library, Search)

/**
 * Convenience wrapper that gives composables a stable navigation API.
 * Delegates all mutation to [Navigator] and reads state from [NavigationState].
 */
class JetcasterAppState(val navigator: Navigator, val navState: NavigationState) {

    val currentTopLevelRoute: NavKey get() = navState.topLevelRoute

    fun navigateToDiscover() = navigator.navigate(Discover)
    fun navigateToLibrary() = navigator.navigate(Library)
    fun navigateToSearch() = navigator.navigate(Search)
    fun navigateToProfile() = navigator.navigate(Profile)
    fun navigateToSettings() = navigator.navigate(Settings)

    fun showPodcastDetails(podcastUri: String) = navigator.navigate(PodcastDetail(podcastUri))
    fun showEpisodeDetails(episodeUri: String) = navigator.navigate(EpisodeDetail(episodeUri))
    fun showEpisodeDetails(episode: PlayerEpisode) = showEpisodeDetails(episode.episodeInfo.uri)

    fun playEpisode() = navigator.navigate(Player)

    fun backToHome() {
        navigator.goBack()
        navigateToDiscover()
    }
}

@Composable
fun rememberJetcasterAppState(): JetcasterAppState {
    val navState = rememberNavigationState(
        startRoute = Discover,
        topLevelRoutes = TOP_LEVEL_ROUTES,
    )
    val navigator = remember { Navigator(navState) }
    return remember(navState, navigator) { JetcasterAppState(navigator, navState) }
}
