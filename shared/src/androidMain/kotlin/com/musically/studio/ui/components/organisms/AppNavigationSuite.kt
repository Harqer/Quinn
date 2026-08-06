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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Add
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationSuite(
    layoutType: NavigationSuiteType,
    currentRoute: NavKey,
    navigator: Navigator,
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
                    // NOTE: Ideally we'd pass chatInputValue to the chat screen or viewmodel here.
                    // For now, we clear the input and navigate.
                    chatInputValue = ""
                    navigator.navigate(Route.Chat)
                },
                onAttachImage = { showChatSheet = false; navigator.navigate(Route.Chat) },
                onVoiceRecord = { showChatSheet = false; navigator.navigate(Route.Chat) },
                onGenerateCoverArt = { showChatSheet = false; navigator.navigate(Route.Chat) },
                onGenerateVideo = { showChatSheet = false; navigator.navigate(Route.Chat) }
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (layoutType == NavigationSuiteType.NavigationBar) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize().padding(bottom = 56.dp)) {
                content()
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
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
                MaveBottomBar(
                    tabs = listOf(
                        MaveBottomNavItemData(
                            title = "Home",
                            icon = Icons.Default.Home,
                            isSelected = currentRoute == Route.Home,
                            onClick = { navigator.navigate(Route.Home) }
                        ),
                        MaveBottomNavItemData(
                            title = "Search",
                            icon = Icons.Default.Search,
                            isSelected = currentRoute == Route.Search,
                            onClick = { navigator.navigate(Route.Search) }
                        ),
                        // Spacer item for center button
                        MaveBottomNavItemData(
                            title = "",
                            icon = Icons.Default.Add,
                            isSelected = false,
                            onClick = { navigator.navigate(Route.Camera) }
                        ),
                        MaveBottomNavItemData(
                            title = "Library",
                            icon = Icons.Default.LibraryMusic,
                            isSelected = currentRoute == Route.Library,
                            onClick = { navigator.navigate(Route.Library) }
                        ),
                        MaveBottomNavItemData(
                            title = "Voice",
                            icon = books_movies_and_music,
                            isSelected = showChatSheet,
                            onClick = { showChatSheet = true }
                        )
                    ),
                    color = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Custom Center Camera Button (Snapchat style)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                    .border(
                        width = 2.dp,
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary,
                                MaterialTheme.colorScheme.primary
                            )
                        ),
                        shape = CircleShape
                    )
                    .debouncedClickable { navigator.navigate(Route.Camera) },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Camera",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
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
                    icon = { Icon(Icons.Default.Add, contentDescription = "Camera") },
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
                    icon = { Icon(books_movies_and_music, contentDescription = "Studio") },
                    label = { Text("Voice") },
                    selected = showChatSheet,
                    onClick = { showChatSheet = true }
                )
            },
            content = {
                Box(modifier = Modifier.fillMaxSize()) {
                    content()
                }
            }
        )
    }
}
