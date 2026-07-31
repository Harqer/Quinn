package com.musically.studio.ui.models

data class AudioDevice(
    val id: String,
    val name: String,
    val type: DeviceType,
    val subtitle: String,
    val isCurrent: Boolean = false,
    val isConnecting: Boolean = false,
    val isShared: Boolean = false
)

enum class DeviceType {
    PHONE, LAPTOP, CAST, SPEAKER, BLUETOOTH
}
