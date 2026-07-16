package com.example.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.N8nApplication
import com.example.data.ChatMessageEntity
import com.example.data.N8nApiClient
import com.example.util.AppLifecycleTracker
import com.example.util.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val chatDao = (application as N8nApplication).database.chatDao()

    val messages: StateFlow<List<ChatMessageEntity>> = chatDao.getAllMessages()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        
        viewModelScope.launch(Dispatchers.IO) {
            val newMsg = ChatMessageEntity(sender = "User", text = text, isSystem = false)
            chatDao.insertMessage(newMsg)
        }

        if (N8nApiClient.webhookUrl.isNotEmpty()) {
            viewModelScope.launch {
                try {
                    val responseMsg = withContext(Dispatchers.IO) {
                        val connection = URL(N8nApiClient.webhookUrl).openConnection() as HttpURLConnection
                        connection.requestMethod = "POST"
                        connection.setRequestProperty("Content-Type", "application/json")
                        connection.setRequestProperty("Accept", "application/json")
                        connection.doOutput = true
                        
                        val jsonInputString = JSONObject().apply {
                            put("message", text)
                        }.toString()
                        
                        OutputStreamWriter(connection.outputStream).use { writer ->
                            writer.write(jsonInputString)
                            writer.flush()
                        }
                        
                        val responseCode = connection.responseCode
                        var reply = "Webhook triggered successfully. ($responseCode)"
                        
                        if (responseCode in 200..299) {
                            try {
                                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                                if (responseText.isNotEmpty()) {
                                    try {
                                        if (responseText.trim().startsWith("[")) {
                                            val jsonArray = org.json.JSONArray(responseText)
                                            if (jsonArray.length() > 0) {
                                                val jsonObj = jsonArray.getJSONObject(0)
                                                reply = jsonObj.optString("output", jsonObj.optString("text", jsonObj.optString("message", jsonObj.optString("data", responseText))))
                                            }
                                        } else {
                                            val json = JSONObject(responseText)
                                            reply = json.optString("output", json.optString("text", json.optString("message", json.optString("data", responseText))))
                                        }
                                    } catch (je: Exception) {
                                        reply = responseText
                                    }
                                }
                            } catch (e: Exception) {
                                // Ignore parsing errors
                            }
                        } else {
                            reply = "Webhook failed with status $responseCode"
                        }
                        connection.disconnect()
                        reply
                    }
                    
                    withContext(Dispatchers.IO) {
                        chatDao.insertMessage(ChatMessageEntity(sender = "System", text = responseMsg, isSystem = true))
                    }
                    
                    if (!AppLifecycleTracker.isForeground) {
                        NotificationHelper.showNotification(getApplication(), "n8n AI Response", responseMsg)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.IO) {
                        chatDao.insertMessage(ChatMessageEntity(sender = "System", text = "Error: ${e.message}", isSystem = true))
                    }
                }
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                chatDao.insertMessage(ChatMessageEntity(sender = "System", text = "Webhook URL not configured.", isSystem = true))
            }
        }
    }
}
