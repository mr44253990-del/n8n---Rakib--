package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.N8nApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(ChatMessage("System", "Webhook AI mode active. You can send messages to your n8n workflow here.", true))
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val newMsg = ChatMessage("User", text, false)
        _messages.value = _messages.value + newMsg

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
                                    reply = responseText
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
                    _messages.value = _messages.value + ChatMessage("System", responseMsg, true)
                } catch (e: Exception) {
                    _messages.value = _messages.value + ChatMessage("System", "Error: ${e.message}", true)
                }
            }
        } else {
            _messages.value = _messages.value + ChatMessage("System", "Webhook URL not configured.", true)
        }
    }
}
