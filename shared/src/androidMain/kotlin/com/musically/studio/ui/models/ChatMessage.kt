/**
 * @AtomicLevel: Atom
 * @SemanticPurpose: Android Component for ChatMessage.kt
 */

package com.musically.studio.ui.models

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val trackId: String? = null
)
