package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.musically.studio.ui.navigation.Route
import com.musically.studio.ui.navigation.Navigator
import kotlinx.coroutines.launch

@Composable
fun AppDrawer(
    drawerState: DrawerState,
    currentRoute: NavKey,
    navigator: Navigator,
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Top
                    )
                )
            ) {
                Spacer(Modifier.height(12.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    selected = currentRoute == Route.Home,
                    onClick = { 
                        navigator.navigate(Route.Home) 
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(PaddingValues(horizontal = 12.dp))
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Podcasts, contentDescription = null) },
                    label = { Text("Discover") },
                    selected = currentRoute == Route.Discover,
                    onClick = { 
                        navigator.navigate(Route.Discover) 
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(PaddingValues(horizontal = 12.dp))
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    label = { Text("Search") },
                    selected = currentRoute == Route.Search,
                    onClick = { 
                        navigator.navigate(Route.Search) 
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(PaddingValues(horizontal = 12.dp))
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
                    label = { Text("Mave") },
                    selected = currentRoute == Route.Chat,
                    onClick = { 
                        navigator.navigate(Route.Chat) 
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(PaddingValues(horizontal = 12.dp))
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Podcasts, contentDescription = null) },
                    label = { Text("Podcast") },
                    selected = currentRoute == Route.Podcast,
                    onClick = { 
                        navigator.navigate(Route.Podcast) 
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(PaddingValues(horizontal = 12.dp))
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null) },
                    label = { Text("Devices") },
                    selected = currentRoute == Route.Devices,
                    onClick = { 
                        navigator.navigate(Route.Devices) 
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(PaddingValues(horizontal = 12.dp))
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.LibraryMusic, contentDescription = null) },
                    label = { Text("Library") },
                    selected = currentRoute == Route.Library,
                    onClick = { 
                        navigator.navigate(Route.Library) 
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(PaddingValues(horizontal = 12.dp))
                )
            }
        },
        content = content
    )
}
