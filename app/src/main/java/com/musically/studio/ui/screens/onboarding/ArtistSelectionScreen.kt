package com.musically.studio.ui.screens.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.components.atoms.MaveButton
import com.musically.studio.ui.components.molecules.MaveArtistCard

data class Artist(val name: String, val imageUrl: String?)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistSelectionScreen(
    viewModel: com.musically.studio.ui.MainViewModel,
    onDone: () -> Unit
) {
    val allArtists = listOf(
        Artist("Taylor Swift", "https://i.scdn.co/image/ab6761610000e5eb859fab694841393c6165c694"),
        Artist("Drake", "https://i.scdn.co/image/ab6761610000e5eb4293385d324db8558179afd9"),
        Artist("The Weeknd", "https://i.scdn.co/image/ab6761610000e5eb214f3cfc684347781b0a501e"),
        Artist("Bad Bunny", "https://i.scdn.co/image/ab6761610000e5eb499d2d46e3929ef31d048f61"),
        Artist("Ed Sheeran", "https://i.scdn.co/image/ab6761610000e5eb12826c47474400787e35ccb7"),
        Artist("Billie Eilish", "https://i.scdn.co/image/ab6761610000e5eb20560a80e1590f6f059f4277")
    )
    
    val selectedArtists = remember { mutableStateListOf<String>() }
    var searchQuery by remember { mutableStateOf("") }

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
                    modifier = Modifier.fillMaxWidth().padding(32.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
            
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(filteredArtists) { artist ->
                    MaveArtistCard(
                        name = artist.name,
                        imageUrl = artist.imageUrl,
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
