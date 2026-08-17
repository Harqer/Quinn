/**
 * @AtomicLevel: Organism
 * @SemanticPurpose: Android Component for AppNavigationSuite.kt
 */

package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.musically.studio.ui.navigation.Navigator
import com.musically.studio.ui.navigation.Route
import com.musically.studio.ui.utils.debouncedClickable
import com.musically.studio.ui.sendTextCommand
import com.musically.studio.ui.startLiveSession
import com.musically.studio.ui.stopLiveSession

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationSuite(
    layoutType: NavigationSuiteType,
    currentRoute: NavKey,
    navigator: Navigator,
    viewModel: com.musically.studio.ui.MainViewModel,
    content: @Composable () -> Unit
) {
    // Android 17 Rule: State Preservation. Strict enforcement of rememberSaveable
    // for UI state so it survives window resizing into floating App Bubbles or Desktop PiP.
    var showChatSheet by rememberSaveable { mutableStateOf(false) }
    var chatInputValue by rememberSaveable { mutableStateOf("") }

    if (showChatSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        ModalBottomSheet(
            onDismissRequest = { showChatSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            ChatInputArea(
                inputValue = chatInputValue,
                onValueChange = { chatInputValue = it },
                onSend = { 
                    showChatSheet = false
                    chatInputValue = ""
                    navigator.navigate(Route.Chat)
                },
                onAttachImage = { showChatSheet = false; navigator.navigate(Route.Chat) },
                onVoiceRecord = { showChatSheet = false; navigator.navigate(Route.Chat) },
                onGenerateCoverArt = { showChatSheet = false; navigator.navigate(Route.Chat) },
                onGenerateVideo = { showChatSheet = false; navigator.navigate(Route.Chat) },
                onGeneratePodcast = { showChatSheet = false; navigator.navigate(Route.Chat) },
                onGenerateAudiobook = { showChatSheet = false; navigator.navigate(Route.Chat) }
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Android 17 Rule: Adaptive Navigation. We unconditionally use NavigationSuiteScaffold
    // which natively adapts between BottomNavigationBar and NavigationRail based on WindowSizeClass.
    NavigationSuiteScaffold(
        layoutType = layoutType,
        navigationSuiteItems = {
            item(
                icon = { Icon(if (currentRoute == Route.Home) Icons.Filled.Home else Icons.Outlined.Home, contentDescription = "Home") },
                label = { Text("Home") },
                selected = currentRoute == Route.Home,
                onClick = { com.musically.studio.ui.utils.executeDebounced { navigator.navigate(Route.Home) } }
            )
            item(
                icon = { Icon(if (currentRoute == Route.Search) Icons.Filled.Search else Icons.Outlined.Search, contentDescription = "Search") },
                label = { Text("Search") },
                selected = currentRoute == Route.Search,
                onClick = { com.musically.studio.ui.utils.executeDebounced { navigator.navigate(Route.Search) } }
            )
            item(
                icon = { Icon(Icons.Default.PhotoCamera, contentDescription = "Camera") },
                label = { Text("Camera") },
                selected = currentRoute == Route.Camera,
                onClick = { com.musically.studio.ui.utils.executeDebounced { navigator.navigate(Route.Camera) } }
            )
            item(
                icon = { Icon(if (currentRoute == Route.Library) Icons.Filled.LibraryMusic else Icons.Outlined.LibraryMusic, contentDescription = "Library") },
                label = { Text("Library") },
                selected = currentRoute == Route.Library,
                onClick = { com.musically.studio.ui.utils.executeDebounced { navigator.navigate(Route.Library) } }
            )
            item(
                icon = { Icon(if (currentRoute == Route.Settings) Icons.Filled.Settings else Icons.Outlined.Settings, contentDescription = "Settings") },
                label = { Text("Settings") },
                selected = currentRoute == Route.Settings,
                onClick = { com.musically.studio.ui.utils.executeDebounced { navigator.navigate(Route.Settings) } }
            )
        },
        // Edge-to-Edge compliance: we let Scaffold manage the core drawing padding.
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()

            // The chatbot overlays the content, anchored at the bottom of the visible content area.
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                MaveChatbotSection(
                    viewModel = viewModel,
                    chatInputValue = chatInputValue,
                    onChatInputValueChange = { chatInputValue = it },
                    onSend = { 
                        if (chatInputValue.isNotBlank()) {
                            viewModel.sendTextCommand(chatInputValue)
                            chatInputValue = ""
                        }
                    },
                    onAttachImage = { },
                    onVoiceRecord = { 
                        if (!viewModel.isLiveSessionActive.value) {
                            viewModel.startLiveSession()
                        } else {
                            viewModel.stopLiveSession()
                        }
                    },
                    onGenerateCoverArt = { },
                    onGenerateVideo = { },
                    onGeneratePodcast = { navigator.navigate(Route.PodcastOnboarding) },
                    onGenerateAudiobook = { navigator.navigate(Route.PodcastOnboarding) }
                )
            }
        }
    }
}
