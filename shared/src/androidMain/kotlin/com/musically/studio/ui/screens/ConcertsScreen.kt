/**
 * @AtomicLevel: Template/Page
 * @SemanticPurpose: Android Component for ConcertsScreen.kt
 */

package com.musically.studio.ui.screens
import androidx.compose.material3.MaterialTheme

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.components.organisms.ConcertCard
import com.musically.studio.ui.concerts
import com.musically.studio.ui.concertSearchError
import com.musically.studio.ui.fetchConcertsNearMe
import com.musically.studio.ui.isSearchingConcerts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConcertsScreen(
    viewModel: MainViewModel,
    onMenuClick: () -> Unit
) {
    val concerts by viewModel.concerts.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearchingConcerts.collectAsStateWithLifecycle()
    val error by viewModel.concertSearchError.collectAsStateWithLifecycle()
    
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    var showLocationDisclosure by remember { mutableStateOf(false) }

    val locationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.fetchConcertsNearMe(searchQuery)
    }

    LaunchedEffect(Unit) {
        val hasCoarsePermission = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (hasCoarsePermission) {
            if (concerts.isEmpty() && !isSearching) {
                viewModel.fetchConcertsNearMe("")
            }
        } else {
            showLocationDisclosure = true
        }
    }

    if (showLocationDisclosure) {
        AlertDialog(
            onDismissRequest = {
                showLocationDisclosure = false
                viewModel.fetchConcertsNearMe("")
            },
            title = { Text("Location Access for Concerts") },
            text = { Text("Mave collects approximate (coarse) location data to show upcoming concerts and musical events near you. Location data is only used for concert search and is never sold or shared for advertising.") },
            confirmButton = {
                TextButton(onClick = {
                    showLocationDisclosure = false
                    locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                }) {
                    Text("Continue")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showLocationDisclosure = false
                    viewModel.fetchConcertsNearMe("")
                }) {
                    Text("Skip")
                }
            }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text("Concerts Near You") },
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 16.dp)) {
                        com.musically.studio.ui.components.atoms.UserAvatarButton(
                            photoUrl = viewModel.getUserPhotoUrl(),
                            displayName = viewModel.getUserDisplayName(),
                            onClick = onMenuClick
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = { Text("Search by artist or venue") },
                trailingIcon = {
                    IconButton(onClick = { viewModel.fetchConcertsNearMe(searchQuery) }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.large
            )
            
            if (isSearching) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: $error", color = MaterialTheme.colorScheme.error)
                }
            } else if (concerts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No concerts found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                    columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(360.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(concerts, key = { it.id }) { concert ->
                        ConcertCard(
                            concert = concert,
                            onClick = { com.musically.studio.ui.utils.executeDebounced {
                                com.musically.studio.ui.utils.SecurityUtils.safeLaunchUrl(context, concert.url)
                            } }
                        )
                    }
                }
            }
        }
    }
}
