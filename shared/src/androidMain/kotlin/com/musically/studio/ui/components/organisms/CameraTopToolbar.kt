package com.musically.studio.ui.components.organisms

import androidx.camera.core.ImageCapture
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musically.studio.ui.components.atoms.CameraLensBadge
import com.musically.studio.ui.screens.CameraAspect

@Composable
fun CameraTopToolbar(
    lensFacing: Int,
    isLowLightBoostEnabled: Boolean,
    isTorchEnabled: Boolean,
    flashMode: Int,
    showExposureSlider: Boolean,
    showGridLines: Boolean,
    currentAspect: CameraAspect,
    onClose: () -> Unit,
    onToggleLowLightBoost: () -> Unit,
    onToggleTorch: () -> Unit,
    onCycleFlashMode: () -> Unit,
    onToggleExposureSlider: () -> Unit,
    onToggleGridLines: () -> Unit,
    onCycleAspectRatio: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                onClick = onClose,
                modifier = Modifier.semantics { contentDescription = "Close Camera" }
            ) {
                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
            }
            CameraLensBadge(lensFacing = lensFacing)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                onClick = onToggleLowLightBoost,
                modifier = Modifier.semantics { contentDescription = "Toggle Low Light Boost" }
            ) {
                Icon(
                    Icons.Default.NightsStay,
                    contentDescription = null,
                    tint = if (isLowLightBoostEnabled) Color.Yellow else Color.White.copy(alpha = 0.6f)
                )
            }

            IconButton(
                onClick = onToggleTorch,
                modifier = Modifier.semantics { contentDescription = "Toggle Torch Flashlight" }
            ) {
                Icon(
                    if (isTorchEnabled) Icons.Default.Highlight else Icons.Default.FlashOff,
                    contentDescription = null,
                    tint = if (isTorchEnabled) Color.Yellow else Color.White
                )
            }

            IconButton(
                onClick = onCycleFlashMode,
                modifier = Modifier.semantics { contentDescription = "Cycle Flash Mode" }
            ) {
                Icon(
                    when (flashMode) {
                        ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn
                        ImageCapture.FLASH_MODE_AUTO -> Icons.Default.FlashAuto
                        else -> Icons.Default.FlashOff
                    },
                    contentDescription = null,
                    tint = if (flashMode != ImageCapture.FLASH_MODE_OFF) Color.Yellow else Color.White
                )
            }

            IconButton(
                onClick = onToggleExposureSlider,
                modifier = Modifier.semantics { contentDescription = "Toggle EV Exposure Adjustment" }
            ) {
                Icon(
                    Icons.Default.Exposure,
                    contentDescription = null,
                    tint = if (showExposureSlider) Color.Yellow else Color.White
                )
            }

            IconButton(
                onClick = onToggleGridLines,
                modifier = Modifier.semantics { contentDescription = "Toggle Composition Grid Lines" }
            ) {
                Icon(
                    Icons.Default.GridOn,
                    contentDescription = null,
                    tint = if (showGridLines) Color.Cyan else Color.White.copy(alpha = 0.6f)
                )
            }

            Surface(
                onClick = onCycleAspectRatio,
                shape = RoundedCornerShape(16.dp),
                color = Color.Black.copy(alpha = 0.5f),
                modifier = Modifier
                    .padding(top = 4.dp)
                    .semantics { contentDescription = "Cycle Aspect Ratio ${currentAspect.ratio}" }
            ) {
                Text(
                    text = currentAspect.ratio,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}
