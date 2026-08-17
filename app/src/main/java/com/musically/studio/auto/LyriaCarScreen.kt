package com.musically.studio.auto

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template

import com.musically.studio.WearableStreamingService

class LyriaCarScreen(carContext: CarContext) : Screen(carContext) {
    private var isRecording = false

    override fun onGetTemplate(): Template {
        val message = if (isRecording) "Listening..." else "Mave Auto is ready."
        val buttonTitle = if (isRecording) "Stop" else "Talk"

        return MessageTemplate.Builder(message)
            .setTitle("Mave / Lyria")
            .addAction(
                Action.Builder()
                    .setTitle(buttonTitle)
                    .setOnClickListener {
                        if (isRecording) {
                            WearableStreamingService.stopVoiceRecording()
                            isRecording = false
                        } else {
                            WearableStreamingService.startVoiceRecording()
                            isRecording = true
                        }
                        invalidate()
                    }
                    .build()
            )
            .build()
    }
}
