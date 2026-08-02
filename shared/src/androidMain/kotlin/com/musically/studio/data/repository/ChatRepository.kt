package com.musically.studio.data.repository

import com.musically.studio.shared.BuildConfig
import com.musically.studio.network.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor() {

    suspend fun loadChatHistory(): JSONObject = withContext(Dispatchers.IO) {
        val token = try { TokenManager.getValidToken() } catch (e: Exception) { null }
        val url = URL("${BuildConfig.API_BASE_URL}/chat/history")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Content-Type", "application/json")
        if (token != null) connection.setRequestProperty("Authorization", "Bearer $token")
        
        val code = connection.responseCode
        val text = if (code in 200..299) connection.inputStream.bufferedReader().readText()
                   else connection.errorStream?.bufferedReader()?.readText() ?: "{\"error\":\"HTTP $code\"}"
        JSONObject(text)
    }

    suspend fun saveChatHistory(body: JSONObject): JSONObject = withContext(Dispatchers.IO) {
        httpPost("/chat/history", body)
    }

    suspend fun executeTool(name: String, args: Map<String, Any?>): JSONObject = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("name", name)
            put("args", JSONObject(args))
        }
        httpPost("/music/execute-tool", body)
    }

    suspend fun generateCover(prompt: String, hq: Boolean): JSONObject = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("prompt", prompt)
            put("hq", hq)
        }
        httpPost("/music/cover", body)
    }
    
    suspend fun generateVideo(prompt: String): JSONObject = withContext(Dispatchers.IO) {
        val body = JSONObject().apply { put("prompt", prompt) }
        httpPost("/music/video", body)
    }

    private suspend fun httpPost(path: String, body: JSONObject): JSONObject = withContext(Dispatchers.IO) {
        val token = try { TokenManager.getValidToken() } catch (e: Exception) { null }
        val url = URL("${BuildConfig.API_BASE_URL}$path")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        if (token != null) connection.setRequestProperty("Authorization", "Bearer $token")
        connection.doOutput = true
        connection.outputStream.use { it.write(body.toString().toByteArray()) }
        
        val code = connection.responseCode
        if (code in 200..299) {
            val responseString = connection.inputStream.bufferedReader().use { it.readText() }
            if (responseString.isEmpty()) JSONObject() else JSONObject(responseString)
        } else {
            throw Exception("HTTP $code")
        }
    }
}
