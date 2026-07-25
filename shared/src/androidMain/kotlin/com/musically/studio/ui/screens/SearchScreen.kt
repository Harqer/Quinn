package com.musically.studio.ui.screens

import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.musically.studio.ui.theme.FormFactorPreviews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.components.atoms.MaveTextField
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: MainViewModel,
    onNavigateToCamera: () -> Unit,
    onNavigateToCategory: (String) -> Unit,
    onNavigateToAlbum: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    val colors = listOf(
        Color(0xFFFF5722), Color(0xFF4CAF50), Color(0xFFE91E63), Color(0xFF2196F3),
        Color(0xFFFFC107), Color(0xFF9C27B0), Color(0xFF00BCD4), Color(0xFF8BC34A),
        Color(0xFFCDDC39), Color(0xFFFF9800), Color(0xFF673AB7), Color(0xFF3F51B5)
    )

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color(0xFF121212),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(Color(0xFF1DB954))
                                .clickable { onNavigateToCamera() /* Settings in real app */ },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "M",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Search",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212)),
                scrollBehavior = scrollBehavior
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
                    
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(150.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding() + 100.dp)
                    ) {
                        items(categories.size) { index ->
                            val category = categories[index]
                            val color = try { category.colorHex?.let { Color(android.graphics.Color.parseColor(it)) } } catch(e: Exception) { null } ?: colors[index % colors.size]
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1.5f)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(color)
                                    .clickable { onNavigateToCategory(category.id) }
                            ) {
                                Text(
                                    text = category.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(12.dp)
                                )
                                // Decorative angled box
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .offset(x = 16.dp, y = 8.dp)
                                        .size(64.dp)
                                        .androidx.compose.ui.draw.rotate(25f)
                                        .background(Color.Black.copy(alpha = 0.2f), MaterialTheme.shapes.small)
                                )
                            }
                        }
                    }
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
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(300.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding() + 100.dp)
                        ) {
                            items(filteredResults.size) { index ->
                                val track = filteredResults[index]
                                com.musically.studio.ui.components.TrackItem(
                                    track = track,
                                    onClick = { viewModel.playTrack(track) },
                                    onAlbumClick = { onNavigateToAlbum(track.album.id) }
                                )
                            }
                        }
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
