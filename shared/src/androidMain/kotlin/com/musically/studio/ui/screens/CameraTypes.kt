package com.musically.studio.ui.screens

import androidx.camera.core.AspectRatio
import androidx.camera.extensions.ExtensionMode

enum class CameraAspect(val ratio: String, val aspectRatioValue: Int) {
    RATIO_1_1("1:1", AspectRatio.RATIO_4_3),
    RATIO_4_3("4:3", AspectRatio.RATIO_4_3),
    RATIO_16_9("16:9", AspectRatio.RATIO_16_9)
}

enum class ActiveExtensionMode(val label: String, val mode: Int) {
    AUTO("Auto", ExtensionMode.AUTO),
    NIGHT("Night", ExtensionMode.NIGHT),
    HDR("HDR", ExtensionMode.HDR),
    BOKEH("Portrait", ExtensionMode.BOKEH)
}

enum class CameraCaptureMode {
    PHOTO,
    VIDEO
}
