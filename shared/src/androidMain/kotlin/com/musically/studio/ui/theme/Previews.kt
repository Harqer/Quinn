/**
 * @AtomicLevel: Atom
 * @SemanticPurpose: Android Component for Previews.kt
 */

package com.musically.studio.ui.theme

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

/**
 * Annotation to generate Compose Previews for different device form factors.
 * Used for adaptive UI testing as recommended by the adaptive skill guidelines.
 */
@Preview(name = "Phone", device = Devices.PHONE, showBackground = true)
@Preview(name = "Foldable", device = Devices.FOLDABLE, showBackground = true)
@Preview(name = "Tablet", device = Devices.TABLET, showBackground = true)
@Preview(name = "Desktop", device = Devices.DESKTOP, showBackground = true)
annotation class FormFactorPreviews
