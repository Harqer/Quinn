package com.musically.studio.engage

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.window.core.layout.WindowSizeClass

fun main() {
    val methods = WindowSizeClass::class.java.methods
    methods.forEach {
        println(it.name + "(" + it.parameterTypes.joinToString { p -> p.name } + ")")
    }
}
