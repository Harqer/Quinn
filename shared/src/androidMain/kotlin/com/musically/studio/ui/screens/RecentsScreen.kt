package com.musically.studio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.playTrack
import com.musically.studio.ui.components.molecules.RecentTrackItem
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentsScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val recentTracks by viewModel.recentTracks.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recently Played") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (recentTracks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No recently played tracks.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            var isRefreshing by remember { mutableStateOf(false) }
            val coroutineScope = rememberCoroutineScope()

            androidx.compose.material3.pulltorefresh.PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    coroutineScope.launch {
                        isRefreshing = true
                        viewModel.loadRecentTracks()
                        isRefreshing = false
                    }
                },
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                    columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(360.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    items(recentTracks, key = { it.id }) { track ->
                        RecentTrackItem(
                            track = track,
                            onClick = { viewModel.playTrack(track) }
                        )
                    }
                }
            }
        }
    }
}
