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
fun AlbumHeader(
    title: String,
    subtitle: String,
    description: String? = null,
    imageUrl: String?,
    albumId: String,
    context: Context,
    isLiked: Boolean = false,
    onLikeClick: () -> Unit,
    onDownloadClick: () -> Unit,
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
            description = description,
            imageUrl = imageUrl,
            isLiked = isLiked,
            onLikeClick = onLikeClick,
            onShareClick = { 
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "Check out this album on Lyria: https://lyria.studio/album/$albumId")
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share Album"))
            },
            onDownloadClick = onDownloadClick,
            onMoreClick = onMoreClick,
            onPlayClick = onPlayClick,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 16.dp)
        )
    }
}
