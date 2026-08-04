package com.musically.studio.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.musically.studio.ui.icons.books_movies_and_music
import com.musically.studio.ui.navigation.Navigator
import com.musically.studio.ui.navigation.Route

@Composable
fun AppNavigationSuite(
    layoutType: NavigationSuiteType,
    currentRoute: NavKey,
    navigator: Navigator,
    content: @Composable () -> Unit
) {
    if (layoutType == NavigationSuiteType.NavigationBar) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize().padding(bottom = 80.dp)) {
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
                                com.musically.studio.ui.theme.MaveBackground.copy(alpha = 0.8f),
                                com.musically.studio.ui.theme.MaveBackground
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
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Home") },
                        selected = currentRoute == Route.Home,
                        onClick = { navigator.navigate(Route.Home) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Search, contentDescription = null) },
                        label = { Text("Search") },
                        selected = currentRoute == Route.Search,
                        onClick = { navigator.navigate(Route.Search) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.LibraryMusic, contentDescription = null) },
                        label = { Text("Your Library") },
                        selected = currentRoute == Route.Library,
                        onClick = { navigator.navigate(Route.Library) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(books_movies_and_music, contentDescription = null) },
                        label = { Text("Create") },
                        selected = currentRoute == Route.Chat,
                        onClick = { navigator.navigate(Route.Chat) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    } else {
        NavigationSuiteScaffold(
            layoutType = layoutType,
            navigationSuiteItems = {
                item(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    selected = currentRoute == Route.Home,
                    onClick = { com.musically.studio.ui.utils.executeDebounced { navigator.navigate(Route.Home) } }
                )
                item(
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    label = { Text("Search") },
                    selected = currentRoute == Route.Search,
                    onClick = { com.musically.studio.ui.utils.executeDebounced { navigator.navigate(Route.Search) } }
                )
                item(
                    icon = { Icon(Icons.Default.LibraryMusic, contentDescription = null) },
                    label = { Text("Your Library") },
                    selected = currentRoute == Route.Library,
                    onClick = { com.musically.studio.ui.utils.executeDebounced { navigator.navigate(Route.Library) } }
                )
                item(
                    icon = { Icon(books_movies_and_music, contentDescription = null) },
                    label = { Text("Create") },
                    selected = currentRoute == Route.Chat,
                    onClick = { com.musically.studio.ui.utils.executeDebounced { navigator.navigate(Route.Chat) } }
                )
            },
            content = content
        )
    }
}
