package com.musically.studio.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.musically.studio.ui.screens.MaveChatTrack
import com.musically.studio.ui.theme.MaveBrand
import com.musically.studio.ui.theme.MaveStyles

@Composable
fun ChatSingleTrackCard(
    track: MaveChatTrack,
    coverArtUrl: String?,
    onClick: (String) -> Unit
) {
    val trackInteractionSource = remember { MutableInteractionSource() }
    val trackStyleState = rememberUpdatedStyleState(trackInteractionSource) { it.isEnabled = true }
    
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(interactionSource = trackInteractionSource, indication = null) {
                if (!track.trackId.isNullOrBlank()) onClick(track.trackId)
            }
            .styleable(styleState = trackStyleState, style = MaveStyles.musicTrackCardStyle),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (coverArtUrl != null) {
            AsyncImage(
                model = coverArtUrl,
                contentDescription = "Track Art",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp))
            )
        } else {
            Box(modifier = Modifier.size(64.dp).background(Color.DarkGray, RoundedCornerShape(8.dp)))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(track.title, fontWeight = FontWeight.Bold)
            Text(track.artist, fontSize = 14.sp)
        }
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Play")
        }
    }
}

@Composable
fun ChatMultiTrackList(
    tracks: List<MaveChatTrack>
) {
    Spacer(modifier = Modifier.height(16.dp))
    Column {
        tracks.forEach { track ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(40.dp).background(MaveBrand.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Album, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(track.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(track.artist, fontSize = 12.sp)
                }
                Icon(Icons.Default.AddCircle, contentDescription = "Add")
            }
        }
    }
}
