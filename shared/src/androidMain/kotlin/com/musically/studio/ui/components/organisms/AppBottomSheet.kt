package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
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
    sheetState: SheetState,
    currentPlayingTrack: MaveTrack?,
    isPlaying: Boolean,
    viewModel: MainViewModel,
    navigator: Navigator,
    content: @Composable (PaddingValues) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val currentModality by viewModel.currentModality.collectAsState()

    // Render the main background content
    Box(modifier = Modifier.fillMaxSize()) {
        content(PaddingValues(0.dp))
    }

    if (currentPlayingTrack != null) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = sheetState,
            onDismissRequest = {
                // If they dismiss, we can stop playback or just hide it
                // For now, let's keep it simple.
            }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (sheetState.targetValue == SheetValue.PartiallyExpanded || sheetState.currentValue == SheetValue.PartiallyExpanded && sheetState.targetValue != SheetValue.Expanded) {
                    MiniPlayer(
                        track = currentPlayingTrack,
                        isPlaying = isPlaying,
                        onPlayPauseClick = { viewModel.togglePlayPause() },
                        onClick = {
                            coroutineScope.launch {
                                sheetState.expand()
                            }
                        }
                    )
                } else {
                    NowPlayingScreen(
                        track = currentPlayingTrack,
                        viewModel = viewModel,
                        modality = currentModality,
                        onCollapse = {
                            coroutineScope.launch {
                                sheetState.partialExpand()
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
                }
            }
        }
    }
}
