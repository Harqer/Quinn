package com.musically.studio.ui.components.organisms

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import com.musically.studio.ui.navigation.Navigator
import com.musically.studio.ui.navigation.Route

@Composable
fun AppNavigationSuite(
    layoutType: NavigationSuiteType,
    currentRoute: NavKey,
    navigator: Navigator,
    content: @Composable () -> Unit
) {
    NavigationSuiteScaffold(
        layoutType = layoutType,
        navigationSuiteItems = {
            item(
                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                label = { Text("Home") },
                selected = currentRoute == Route.Home,
                onClick = { navigator.navigate(Route.Home) }
            )
            item(
                icon = { Icon(Icons.Default.Podcasts, contentDescription = null) },
                label = { Text("Discover") },
                selected = currentRoute == Route.Discover,
                onClick = { navigator.navigate(Route.Discover) }
            )
            item(
                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
                label = { Text("Mave") },
                selected = currentRoute == Route.Chat,
                onClick = { navigator.navigate(Route.Chat) }
            )
            item(
                icon = { Icon(Icons.Default.Podcasts, contentDescription = null) },
                label = { Text("Podcast") },
                selected = currentRoute == Route.Podcast,
                onClick = { navigator.navigate(Route.Podcast) }
            )
            item(
                icon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null) },
                label = { Text("Devices") },
                selected = currentRoute == Route.Devices,
                onClick = { navigator.navigate(Route.Devices) }
            )
            item(
                icon = { Icon(Icons.Default.LibraryMusic, contentDescription = null) },
                label = { Text("Library") },
                selected = currentRoute == Route.Library,
                onClick = { navigator.navigate(Route.Library) }
            )
        },
        content = content
    )
}
