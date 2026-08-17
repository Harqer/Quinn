/**
 * @AtomicLevel: Atom
 * @SemanticPurpose: Android Component for CameraLensBadge.kt
 */

package com.musically.studio.ui.components.atoms

import androidx.camera.core.CameraSelector
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CameraLensBadge(
    lensFacing: Int,
    modifier: Modifier = Modifier
) {
    val badgeText = if (lensFacing == CameraSelector.LENS_FACING_FRONT) "Front (Selfie)" else "Rear Lens"
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.Black.copy(alpha = 0.5f),
        modifier = modifier.semantics(mergeDescendants = true) {
            contentDescription = badgeText
        }
    ) {
        Text(
            text = badgeText,
            color = if (lensFacing == CameraSelector.LENS_FACING_FRONT) Color.Cyan else Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
