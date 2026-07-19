package com.musically.studio

import com.meta.wearable.dat.display.views.*

object WearableUi {
    fun mainDashboard(
        geminiResponse: String,
        onSpeak: () -> Unit,
        onCreateMusic: () -> Unit,
        onPlay: () -> Unit,
        onPause: () -> Unit,
        onBack: () -> Unit
    ): ContentScope.() -> Unit = {
        flexBox(
            direction = Direction.COLUMN,
            alignment = Alignment.CENTER,
            background = FlexBoxBackground.CARD
        ) {
            flexBox(
                direction = Direction.ROW,
                alignment = Alignment.CENTER
            ) {
                button("Back", ButtonStyle.SECONDARY, IconName.ARROW_LEFT, onBack)
                text(" Gemini ", TextStyle.HEADING, TextColor.PRIMARY)
            }
            
            text(geminiResponse, TextStyle.BODY, TextColor.PRIMARY)
            
            flexBox(
                direction = Direction.ROW,
                alignment = Alignment.CENTER
            ) {
                button("Speak", ButtonStyle.PRIMARY, IconName.META_AI, onSpeak)
                button("Create", ButtonStyle.SECONDARY, IconName.MUSIC_NOTE, onCreateMusic)
                button("Play", ButtonStyle.SECONDARY, IconName.TRIANGLE_RIGHT, onPlay)
                button("Pause", ButtonStyle.SECONDARY, IconName.TWO_LINES_PARALLEL, onPause)
            }
        }
    }
}
