package com.musically.studio.ui.components.organisms

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musically.studio.ui.components.MediaCoverCard

@Composable
fun PlaylistHeader(
    title: String,
    subtitle: String,
    imageUrl: String?,
    playlistId: String,
    context: Context,
    onLikeClick: () -> Unit,
    onMoreClick: () -> Unit,
    onPlayClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        MediaCoverCard(
            title = title,
            subtitle = subtitle,
            imageUrl = imageUrl,
            isLiked = false,
            onLikeClick = onLikeClick,
            onShareClick = { 
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "Check out this playlist on Mave: https://mave.studio/playlist/$playlistId")
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share Playlist"))
            },
            onMoreClick = onMoreClick,
            onPlayClick = onPlayClick,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
