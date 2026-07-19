package com.musically.studio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.musically.studio.network.SpotifyTrack
import com.musically.studio.ui.theme.MusicallyTheme

@Composable
fun HomeScreen(
    isSpotifyConnected: Boolean,
    tracks: List<SpotifyTrack>,
    onConnectSpotify: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (!isSpotifyConnected) {
            SpotifyEmptyState(onConnectSpotify)
        } else {
            TrackList(tracks)
        }
    }
}

@Composable
private fun SpotifyEmptyState(onConnectSpotify: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No Music Found",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Connect your Spotify account to sync your favorite albums and tracks directly into Musically.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onConnectSpotify,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.styleable(null, MusicallyTheme.styles.primaryButtonStyle)
        ) {
            Text(text = "Connect to Spotify", color = Color.White)
        }
    }
}

@Composable
private fun TrackList(tracks: List<SpotifyTrack>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tracks) { track ->
            TrackItem(track)
        }
    }
}

@Composable
private fun TrackItem(track: SpotifyTrack) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            color = Color.DarkGray,
            shape = MaterialTheme.shapes.small
        ) {}
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = track.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = track.artists.joinToString { it.name },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
