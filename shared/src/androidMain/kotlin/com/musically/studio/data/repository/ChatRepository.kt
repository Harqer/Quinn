package com.musically.studio.data.repository

import com.musically.studio.dataconnect.DefaultConnector
import com.musically.studio.dataconnect.*
import com.musically.studio.shared.BuildConfig
import com.musically.studio.network.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONArray
import com.google.firebase.functions.functions
import com.google.firebase.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val connector: DefaultConnector
) {

    suspend fun loadChatHistory(): JSONObject = withContext(Dispatchers.IO) {
        try {
            val response = connector.listChatMessages.execute(userId = "current_user")
            val array = JSONArray()
            response.data.chatMessages.forEach { msg ->
                val obj = JSONObject()
                obj.put("role", msg.sender)
                obj.put("content", msg.text)
                array.put(obj)
            }
            JSONObject().apply { put("messages", array) }
        } catch (e: Exception) {
            JSONObject().apply { put("error", e.message) }
        }
    }

    suspend fun saveChatHistory(body: JSONObject): JSONObject = withContext(Dispatchers.IO) {
        try {
            val role = body.optString("role", "user")
            val content = body.optString("content", "")
            connector.addChatMessage.execute(
                userId = "current_user",
                sender = role,
                text = content
            )
            JSONObject().apply { put("success", true) }
        } catch (e: Exception) {
            JSONObject().apply { put("error", e.message) }
        }
    }

    suspend fun executeTool(name: String, args: Map<String, Any?>): JSONObject = withContext(Dispatchers.IO) {
        val payload = mapOf("name" to name, "args" to args)
        try {
            val result = Firebase.functions.getHttpsCallable("executeTool").call(payload).await()
            val data = result.data as? Map<*, *>
            if (data != null) JSONObject(data) else JSONObject().apply { put("result", result.data.toString()) }
        } catch (e: Exception) {
            JSONObject().apply { put("error", e.message) }
        }
    }

    suspend fun generateCover(prompt: String, hq: Boolean): JSONObject = withContext(Dispatchers.IO) {
        val payload = mapOf("prompt" to prompt, "hq" to hq)
        try {
            val result = Firebase.functions.getHttpsCallable("generateVisualMedia").call(payload).await()
            val data = result.data as? Map<*, *>
            if (data != null) JSONObject(data) else JSONObject().apply { put("result", result.data.toString()) }
        } catch (e: Exception) {
            JSONObject().apply { put("error", e.message) }
        }
    }
    
    suspend fun generateVideo(prompt: String): JSONObject = withContext(Dispatchers.IO) {
        val payload = mapOf("prompt" to prompt)
        try {
            val result = Firebase.functions.getHttpsCallable("executeTool").call(mapOf("name" to "generate_music_video", "args" to payload)).await()
            val data = result.data as? Map<*, *>
            if (data != null) JSONObject(data) else JSONObject().apply { put("result", result.data.toString()) }
        } catch (e: Exception) {
            JSONObject().apply { put("error", e.message) }
        }
    }
}
