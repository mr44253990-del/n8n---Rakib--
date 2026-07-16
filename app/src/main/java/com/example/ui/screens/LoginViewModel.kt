package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.data.N8nApiClient
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class LoginViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun loginWithEmail(url: String, email: String, pass: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            if (!url.startsWith("http")) {
                _loginState.value = LoginState.Error("URL must start with http or https")
                return@launch
            }
            try {
                val baseUrl = url.trimEnd('/')
                var setCookie: String? = null
                val success = withContext(Dispatchers.IO) {
                    val connection = URL("$baseUrl/rest/login").openConnection() as HttpURLConnection
                    connection.requestMethod = "POST"
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.setRequestProperty("Accept", "application/json")
                    connection.doOutput = true

                    val jsonInputString = JSONObject().apply {
                        put("email", email)
                        put("password", pass)
                    }.toString()

                    OutputStreamWriter(connection.outputStream).use { writer ->
                        writer.write(jsonInputString)
                        writer.flush()
                    }

                    val responseCode = connection.responseCode
                    if (responseCode in 200..299) {
                        setCookie = connection.getHeaderField("Set-Cookie")
                    }
                    connection.disconnect()
                    responseCode in 200..299
                }

                if (success) {
                    N8nApiClient.baseUrl = baseUrl
                    N8nApiClient.authMode = 0
                    if (setCookie != null) {
                        N8nApiClient.cookie = setCookie!!
                    }
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error("Invalid email or password.")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Network error: ${e.message}")
            }
        }
    }

    fun loginWithApiKey(url: String, apiKey: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            if (!url.startsWith("http")) {
                _loginState.value = LoginState.Error("URL must start with http or https")
                return@launch
            }
            try {
                val baseUrl = url.trimEnd('/')
                val success = withContext(Dispatchers.IO) {
                    val connection = URL("$baseUrl/api/v1/workflows").openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.setRequestProperty("X-N8N-API-KEY", apiKey)
                    connection.setRequestProperty("Accept", "application/json")

                    val responseCode = connection.responseCode
                    connection.disconnect()
                    responseCode in 200..299
                }

                if (success) {
                    N8nApiClient.baseUrl = baseUrl
                    N8nApiClient.apiKey = apiKey
                    N8nApiClient.authMode = 1
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error("Invalid API Key or URL.")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Network error: ${e.message}")
            }
        }
    }

    fun testWebhook(url: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            if (!url.startsWith("http")) {
                _loginState.value = LoginState.Error("URL must start with http or https")
                return@launch
            }
            try {
                val success = withContext(Dispatchers.IO) {
                    val connection = URL(url).openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    val responseCode = connection.responseCode
                    connection.disconnect()
                    responseCode in 200..405 // Anything that's not a generic network failure
                }
                
                if (success) {
                   N8nApiClient.webhookUrl = url
                   N8nApiClient.authMode = 2
                   _loginState.value = LoginState.Success
                } else {
                   _loginState.value = LoginState.Error("Invalid Webhook URL. Server error.")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Network error: ${e.message}")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
