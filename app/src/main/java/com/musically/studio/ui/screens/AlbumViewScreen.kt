package com.musically.studio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.musically.studio.R
import com.musically.studio.network.SpotifyTrack
import com.musically.studio.ui.MainViewModel
import com.musically.studio.ui.theme.SpotifyBlack
import com.musically.studio.ui.theme.SpotifyGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumViewScreen(
    albumId: String,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onTrackClick: (String) -> Unit
) {
    val tracks by viewModel.tracks.collectAsState()
    val context = LocalContext.current
    val albumTracks = tracks.filter { it.album?.id == albumId }
    val albumInfo = albumTracks.firstOrNull()?.album
    val moreOptionsText = stringResource(id = R.string.more_options)

    Scaffold(
        containerColor = SpotifyBlack,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = albumInfo?.images?.firstOrNull()?.url,
                        contentDescription = stringResource(id = R.string.album_art_content_desc),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.DarkGray)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = albumInfo?.name ?: stringResource(id = R.string.unknown_album),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(id = R.string.album_bullet, albumInfo?.artists?.joinToString { it.name } ?: stringResource(id = R.string.unknown_artist)),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.LightGray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            IconButton(onClick = { albumTracks.firstOrNull()?.let { viewModel.bookmarkTrack(it.id) } }) {
                                Icon(Icons.Default.FavoriteBorder, contentDescription = stringResource(id = R.string.like_content_desc), tint = Color.White)
                            }
                            IconButton(onClick = { 
                                Toast.makeText(context, moreOptionsText, Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.MoreVert, contentDescription = stringResource(id = R.string.more_content_desc), tint = Color.White)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(SpotifyGreen, CircleShape)
                                .clickable { 
                                    albumTracks.firstOrNull()?.let { onTrackClick(it.id) }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = stringResource(id = R.string.play_content_desc), tint = SpotifyBlack, modifier = Modifier.size(32.dp))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            itemsIndexed(albumTracks) { index, track ->
                AlbumTrackItem(
                    track = track,
                    trackNumber = index + 1,
                    onClick = { onTrackClick(track.id) },
                    moreOptionsText = moreOptionsText
                )
            }
        }
    }
}

@Composable
fun AlbumTrackItem(
    track: SpotifyTrack,
    trackNumber: Int,
    onClick: () -> Unit,
    moreOptionsText: String
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = trackNumber.toString(),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.width(32.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.name,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                maxLines = 1
            )
            Text(
                text = track.artists?.joinToString { it.name } ?: stringResource(id = R.string.unknown),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1
            )
        }
        IconButton(onClick = { 
            Toast.makeText(context, moreOptionsText, Toast.LENGTH_SHORT).show()
        }) {
            Icon(Icons.Default.MoreVert, contentDescription = stringResource(id = R.string.more_content_desc), tint = Color.Gray)
        }
    }
}
