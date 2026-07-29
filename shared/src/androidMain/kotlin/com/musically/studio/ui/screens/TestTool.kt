package com.musically.studio.ui.screens

import com.google.firebase.vertexai.type.defineFunction
import com.google.firebase.vertexai.type.Schema
import org.json.JSONObject

val function = defineFunction(
    "name",
    "description",
    Schema.str("prompt", "desc")
) { prompt -> JSONObject() }
