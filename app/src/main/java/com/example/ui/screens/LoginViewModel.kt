package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.ktor.client.statement.HttpResponse

@Serializable
data class LoginRequest(val email: String, val password: String)

class LoginViewModel : ViewModel() {
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun loginWithEmail(url: String, email: String, pass: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val baseUrl = url.trimEnd('/')
                val response: HttpResponse = client.post("$baseUrl/rest/login") {
                    contentType(ContentType.Application.Json)
                    setBody(LoginRequest(email, pass))
                }
                if (response.status.isSuccess()) {
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
            try {
                val baseUrl = url.trimEnd('/')
                val response: HttpResponse = client.get("$baseUrl/api/v1/workflows") {
                    header("X-N8N-API-KEY", apiKey)
                }
                
                if (response.status.isSuccess()) {
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error("Invalid API Key or URL. Code: ${response.status.value}")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Network error: ${e.message}")
            }
        }
    }

    fun testWebhook(url: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                if (url.startsWith("http")) {
                   _loginState.value = LoginState.Success
                } else {
                   _loginState.value = LoginState.Error("Invalid Webhook URL format. Must start with http.")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error: ${e.message}")
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
