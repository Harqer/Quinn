package com.musically.studio

import com.meta.wearable.dat.display.views.*

object WearableUi {
    fun mainDashboard(vibeTitle: String, onPlay: () -> Unit, onPause: () -> Unit): ContentScope.() -> Unit = {
        flexBox(
            direction = Direction.COLUMN,
            alignment = Alignment.CENTER,
            background = FlexBoxBackground.CARD
        ) {
            text("Musically Live", TextStyle.HEADING, TextColor.PRIMARY)
            text("Current Vibe:", TextStyle.META, TextColor.SECONDARY)
            text(vibeTitle, TextStyle.BODY, TextColor.PRIMARY)
            
            flexBox(
                direction = Direction.ROW,
                alignment = Alignment.CENTER
            ) {
                button("Play", ButtonStyle.PRIMARY, IconName.TRIANGLE_RIGHT, onPlay)
                button("Pause", ButtonStyle.SECONDARY, IconName.TWO_LINES_PARALLEL, onPause)
            }
        }
    }
}
