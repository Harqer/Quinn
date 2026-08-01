package com.musically.studio.ui.screens

import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.musically.studio.ui.theme.FormFactorPreviews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.ui.components.organisms.SearchCategoryGrid
import com.musically.studio.ui.components.organisms.SearchResultsGrid
import com.musically.studio.ui.components.organisms.SearchTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: MainViewModel,
    onNavigateToCamera: () -> Unit,
    onNavigateToCategory: (String) -> Unit,
    onNavigateToAlbum: (String) -> Unit,
    onNavigateToSettings: () -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    val colors = listOf(
        com.musically.studio.ui.theme.MaveDeepOrange500, com.musically.studio.ui.theme.MaveGreen500, com.musically.studio.ui.theme.MavePink500, com.musically.studio.ui.theme.MaveBlue500,
        com.musically.studio.ui.theme.MaveAmber500, com.musically.studio.ui.theme.MavePurple700, com.musically.studio.ui.theme.MaveCyanMaterial500, com.musically.studio.ui.theme.MaveLightGreen500,
        com.musically.studio.ui.theme.MaveLime500, com.musically.studio.ui.theme.MaveOrange500, com.musically.studio.ui.theme.MaveDeepPurple500, com.musically.studio.ui.theme.MaveIndigo500
    )

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = com.musically.studio.ui.theme.MaveBackground,
        topBar = {
            SearchTopBar(
                scrollBehavior = scrollBehavior,
                onNavigateToSettings = onNavigateToSettings
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .imePadding()
            ) {
                Spacer(modifier = Modifier.height(paddingValues.calculateTopPadding() + 16.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("What do you want to listen to?", color = Color.Black.copy(alpha = 0.6f)) },
                    leadingIcon = { 
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Black)
                    },
                    trailingIcon = null,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.small,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (query.isEmpty()) {
                    Text(
                        text = "Browse all",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                    )
                    
                    SearchCategoryGrid(
                        categories = categories,
                        colors = colors,
                        contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding() + 100.dp),
                        onNavigateToCategory = onNavigateToCategory
                    )
                } else {
                    val searchResults by viewModel.communityTracks.collectAsStateWithLifecycle()
                    val filteredResults = searchResults.filter {
                        it.name.contains(query, ignoreCase = true) || 
                        it.artists.any { artist -> artist.name.contains(query, ignoreCase = true) }
                    }
                    
                    Text(
                        text = "Search results for \"$query\"",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                    )
                    
                    if (filteredResults.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize().padding(top = 32.dp), contentAlignment = Alignment.TopCenter) {
                            Text("No tracks found", color = Color.White.copy(alpha = 0.7f))
                        }
                    } else {
                        SearchResultsGrid(
                            filteredResults = filteredResults,
                            contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding() + 100.dp),
                            onPlayTrack = { viewModel.playTrack(it) },
                            onNavigateToAlbum = onNavigateToAlbum
                        )
                    }
                }
            }
        }
    }
}

@FormFactorPreviews
@Composable
fun SearchScreenPreview() {
    MaterialTheme {
        SearchScreen(
            viewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
            onNavigateToCamera = {},
            onNavigateToCategory = {},
            onNavigateToAlbum = {}
        )
    }
}
