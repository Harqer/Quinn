package com.musically.studio.glasses

import com.meta.wearable.dat.display.Display
import com.meta.wearable.dat.display.views.ButtonStyle
import com.meta.wearable.dat.display.views.FlexBoxBackground
import com.meta.wearable.dat.display.views.IconName
import com.meta.wearable.dat.display.views.TextColor
import com.meta.wearable.dat.display.views.TextStyle
import com.meta.wearable.dat.display.views.IconStyle

import timber.log.Timber

/**
 * Controller for managing the UI displayed on the Meta Wearables glasses.
 * Adheres to the AGENTS.md rule: exactly one root view (flexBox or video) per sendContent.
 */
class GlassesUIController(private val display: Display) {

    /**
     * Shows a status card on the glasses while the AI is listening or generating.
     */
    suspend fun showStatusCard(status: String, description: String, iconName: IconName = IconName.GEAR) {
        display.sendContent {
            flexBox(
                gap = 12,
                padding = 24,
                background = FlexBoxBackground.CARD,
                onClick = { /* No-op by default */ }
            ) {
                icon(name = iconName, style = IconStyle.FILLED)
                text(status, style = TextStyle.HEADING)
                text(description, style = TextStyle.BODY, color = TextColor.SECONDARY)
            }
        }.onFailure { error, _ ->
            Timber.e("GlassesUIController: Failed to show status card: $error")
        }
    }

    /**
     * Shows a completed track card with interactive buttons routed back to the phone.
     */
    suspend fun showTrackCompleteCard(trackName: String, onPlayClick: () -> Unit, onRemixClick: () -> Unit) {
        display.sendContent {
            flexBox(
                gap = 12,
                padding = 24,
                background = FlexBoxBackground.CARD,
                onClick = { /* No-op */ }
            ) {
                icon(name = IconName.CHECKMARK, style = IconStyle.FILLED)
                text(trackName, style = TextStyle.HEADING)
                
                button(
                    label = "Play",
                    style = ButtonStyle.PRIMARY,
                    iconName = IconName.CHECKMARK,
                    onClick = onPlayClick
                )
                
                button(
                    label = "Remix (Steer)",
                    style = ButtonStyle.SECONDARY,
                    iconName = IconName.GEAR,
                    onClick = onRemixClick
                )
            }
        }.onFailure { error, _ ->
            Timber.e("GlassesUIController: Failed to show track complete card: $error")
        }
    }
}
