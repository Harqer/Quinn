package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.musically.studio.ui.icons.books_movies_and_music
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
    var showChatSheet by remember { mutableStateOf(false) }
    var chatInputValue by remember { mutableStateOf("") }

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

    if (layoutType == NavigationSuiteType.NavigationBar) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize().navigationBarsPadding().padding(bottom = 80.dp)) {
                content()
            }

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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                    MaterialTheme.colorScheme.surface
                                ),
                                startY = 0f
                            )
                        )
                ) {
                    NavigationBar(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                if (currentRoute == Route.Home) Icons.Filled.Home else Icons.Outlined.Home, 
                                contentDescription = "Home"
                            ) 
                        },
                        label = { Text("Home") },
                        selected = currentRoute == Route.Home,
                        onClick = { navigator.navigate(Route.Home) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                if (currentRoute == Route.Search) Icons.Filled.Search else Icons.Outlined.Search, 
                                contentDescription = "Search"
                            ) 
                        },
                        label = { Text("Search") },
                        selected = currentRoute == Route.Search,
                        onClick = { navigator.navigate(Route.Search) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                if (currentRoute == Route.Camera) Icons.Filled.PhotoCamera else Icons.Default.PhotoCamera, 
                                contentDescription = "Camera"
                            ) 
                        },
                        label = { Text("Camera") },
                        selected = currentRoute == Route.Camera,
                        onClick = { navigator.navigate(Route.Camera) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White
                        )
                    )
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                if (currentRoute == Route.Library) Icons.Filled.LibraryMusic else Icons.Outlined.LibraryMusic, 
                                contentDescription = "Library"
                            ) 
                        },
                        label = { Text("Library") },
                        selected = currentRoute == Route.Library,
                        onClick = { navigator.navigate(Route.Library) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                if (currentRoute == Route.Settings) Icons.Filled.Settings else Icons.Outlined.Settings, 
                                contentDescription = "Settings"
                            ) 
                        },
                        label = { Text("Settings") },
                        selected = currentRoute == Route.Settings,
                        onClick = { navigator.navigate(Route.Settings) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                    // Removed Voice tab as per user request
                }

            }
            }
        }
    } else {
        NavigationSuiteScaffold(
            layoutType = layoutType,
            navigationSuiteItems = {
                item(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = currentRoute == Route.Home,
                    onClick = { com.musically.studio.ui.utils.executeDebounced { navigator.navigate(Route.Home) } }
                )
                item(
                    icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
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
                    icon = { Icon(Icons.Default.LibraryMusic, contentDescription = "Library") },
                    label = { Text("Your Library") },
                    selected = currentRoute == Route.Library,
                    onClick = { com.musically.studio.ui.utils.executeDebounced { navigator.navigate(Route.Library) } }
                )
                item(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = currentRoute == Route.Settings,
                    onClick = { com.musically.studio.ui.utils.executeDebounced { navigator.navigate(Route.Settings) } }
                )
                // Removed Voice tab as per user request
            },
            content = {
                Box(modifier = Modifier.fillMaxSize()) {
                    content()
                }
            }
        )
    }
}
