package com.musically.studio.services

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.FunctionCallPart
import com.google.firebase.ai.type.FunctionDeclaration
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.content
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatGenerativeService @Inject constructor() {
    
    private val tools = listOf(
        Tool.functionDeclarations(
            listOf(
                FunctionDeclaration(
                    "generate_full_track",
                    "Generate a new, complete professional music track or song using Lyria 3. Use when the user wants a full song created from scratch.",
                    mapOf("prompt" to Schema.string("Musical style, genre, and description of the full song to create"))
                ),
                FunctionDeclaration(
                    "tweak_instrumentation",
                    "Modify or tweak the instruments, density, BPM, brightness, or style of the currently playing track in real-time using Lyria RealTime. Use when the user wants to change how the song sounds without regenerating from scratch.",
                    mapOf("prompt" to Schema.string("What to tweak (e.g. add more bass, make it faster, add jazz piano)"))
                ),
                FunctionDeclaration(
                    "generate_cover_art",
                    "Generate or update the album cover art for the current track.",
                    mapOf("prompt" to Schema.string("Visual description for the cover art"))
                ),
                FunctionDeclaration(
                    "generate_video",
                    "Generate a music video for the current track. Only use when the user explicitly asks for a video.",
                    mapOf("prompt" to Schema.string("Visual and cinematic description for the music video"))
                )
            )
        )
    )

    private val generativeModel = Firebase.ai.generativeModel(
        modelName = "gemini-3.1-flash",
        systemInstruction = content { text("You are Mave, the Executive Creative Director and Master Musical Orchestrator. Please put your thoughts in <think> and </think> tags. Use maximum reasoning effort and ultrathink step by step. Provide a raw, unstructured, stream-of-consciousness thinking process. Do NOT use numbered lists or formal steps. Do NOT prefix with 'Thinking Process:'. After the closing </think> tag, respond in natural, conversational text ONLY. Do NOT use any markdown formatting.") },
        tools = tools
    )
    
    fun generateContentStream(prompt: String): Flow<com.google.firebase.ai.type.GenerateContentResponse> {
        return generativeModel.generateContentStream(prompt)
    }
}
