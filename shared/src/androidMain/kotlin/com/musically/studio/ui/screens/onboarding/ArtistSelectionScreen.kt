package com.musically.studio.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.components.atoms.MaveButton
import com.musically.studio.ui.components.atoms.MaveTextField
import com.musically.studio.ui.components.molecules.MaveArtistCard
import com.musically.studio.ui.theme.MaveStyles
import androidx.compose.foundation.style.styleable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistSelectionScreen(
    viewModel: MainViewModel,
    onDone: () -> Unit
) {
    val tracks by viewModel.communityTracks.collectAsStateWithLifecycle()
    val allArtists = remember(tracks) {
        tracks.flatMap { it.artists }.distinctBy { it.id }
    }
    
    val selectedArtists = remember { mutableStateListOf<String>() }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchCommunityTracks()
    }

    val filteredArtists = if (searchQuery.isBlank()) {
        allArtists
    } else {
        allArtists.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (selectedArtists.size >= 3) {
                MaveButton(
                    text = "Done",
                    onClick = { 
                        viewModel.saveArtistPreferences(selectedArtists.toList()) {
                            onDone()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    style = MaveStyles.primaryButton
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Choose 3 or more artists you like.",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            MaveTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = "Search",
                trailingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (allArtists.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No artists found", color = Color.White, style = MaterialTheme.typography.titleMedium)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    items(filteredArtists) { artist ->
                        MaveArtistCard(
                            name = artist.name,
                            imageUrl = null,
                            isSelected = selectedArtists.contains(artist.name),
                            onClick = {
                                if (selectedArtists.contains(artist.name)) {
                                    selectedArtists.remove(artist.name)
                                } else {
                                    selectedArtists.add(artist.name)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
