/**
 * @AtomicLevel: Atom
 * @SemanticPurpose: Android Component for POVView.kt
 */

package com.musically.studio.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.musically.studio.WearableStreamingService
import kotlinx.coroutines.flow.collectLatest

@Composable
fun POVView(modifier: Modifier = Modifier) {
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(Unit) {
        WearableStreamingService.cameraFrames.collectLatest { bytes ->
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
    }

    Surface(
        modifier = modifier.size(280.dp, 210.dp),
        color = Color.Black,
        shape = MaterialTheme.shapes.medium
    ) {
        val currentBitmap = bitmap
        if (currentBitmap != null) {
            Image(
                bitmap = currentBitmap.asImageBitmap(),
                contentDescription = "Wearable POV",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}
