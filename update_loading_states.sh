#!/bin/bash
sed -i 's/val hasSeenTooltipTour by viewModel.hasSeenTooltipTour.collectAsStateWithLifecycle()/val hasSeenTooltipTour by viewModel.hasSeenTooltipTour.collectAsStateWithLifecycle()\n    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()/' shared/src/androidMain/kotlin/com/musically/studio/ui/screens/DiscoverScreen.kt

sed -i 's/communityTracks = communityTracks,/communityTracks = communityTracks,\n                        isLoading = isLoading,/' shared/src/androidMain/kotlin/com/musically/studio/ui/screens/DiscoverScreen.kt

sed -i 's/playlists = playlists,/playlists = playlists,\n                    isLoading = isLoading,/' shared/src/androidMain/kotlin/com/musically/studio/ui/screens/DiscoverScreen.kt

sed -i 's/communityTracks: List<MaveTrack>,/communityTracks: List<MaveTrack>,\n    isLoading: Boolean = false,/' shared/src/androidMain/kotlin/com/musically/studio/ui/components/organisms/FreshReleasesSection.kt

sed -i 's/if (errorMessage != null && communityTracks.isEmpty()) {/if (isLoading && communityTracks.isEmpty()) {\n            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {\n                androidx.compose.material3.CircularProgressIndicator(color = Color.White)\n            }\n        } else if (errorMessage != null && communityTracks.isEmpty()) {/' shared/src/androidMain/kotlin/com/musically/studio/ui/components/organisms/FreshReleasesSection.kt

sed -i 's/playlists: List<MavePlaylist>,/playlists: List<MavePlaylist>,\n    isLoading: Boolean = false,/' shared/src/androidMain/kotlin/com/musically/studio/ui/components/organisms/FeaturedPlaylistsSection.kt

sed -i 's/if (errorMessage != null && playlists.isEmpty()) {/if (isLoading && playlists.isEmpty()) {\n        item(span = { GridItemSpan(maxLineSpan) }) {\n            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {\n                androidx.compose.material3.CircularProgressIndicator(color = Color.White)\n            }\n        }\n    } else if (errorMessage != null && playlists.isEmpty()) {/' shared/src/androidMain/kotlin/com/musically/studio/ui/components/organisms/FeaturedPlaylistsSection.kt

