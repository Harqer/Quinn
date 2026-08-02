package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musically.studio.network.MaveTrack
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.togglePlayPause
import com.musically.studio.ui.components.MiniPlayer
import com.musically.studio.ui.navigation.Navigator
import com.musically.studio.ui.navigation.Route
import com.musically.studio.ui.screens.NowPlayingScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBottomSheet(
    scaffoldState: BottomSheetScaffoldState,
    currentPlayingTrack: MaveTrack?,
    isPlaying: Boolean,
    viewModel: MainViewModel,
    navigator: Navigator,
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    // Current modality logic is assumed to be accessible from the viewModel.
    // For purity, we fetch it here or pass it in. We'll pass it from the parent or read it here.
    val currentModality by viewModel.currentModality.collectAsState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = if (currentPlayingTrack != null) 72.dp else 0.dp,
        sheetDragHandle = null,
        sheetContent = {
            if (currentPlayingTrack != null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    NowPlayingScreen(
                        track = currentPlayingTrack,
                        viewModel = viewModel,
                        modality = currentModality,
                        onCollapse = {
                            coroutineScope.launch {
                                scaffoldState.bottomSheetState.partialExpand()
                            }
                        },
                        onMoreOptions = { navigator.navigate(Route.TrackOptions(currentPlayingTrack.id)) },
                        onQueueClick = { 
                            navigator.navigate(Route.Queue)
                        },
                        onLyricsClick = { 
                            navigator.navigate(Route.Lyrics(currentPlayingTrack.id))
                        },
                        onDeviceClick = {
                            navigator.navigate(Route.Devices)
                        }
                    )
                    if (scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
                        MiniPlayer(
                            track = currentPlayingTrack,
                            isPlaying = isPlaying,
                            onPlayPauseClick = { viewModel.togglePlayPause() },
                            onClick = {
                                coroutineScope.launch {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            }
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.height(1.dp))
            }
        },
        content = content
    )
}
