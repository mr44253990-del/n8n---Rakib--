package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.net.HttpURLConnection
import java.net.URL

object N8nApiClient {
    var baseUrl: String = ""
    var apiKey: String = ""
    var cookie: String = ""
    var authMode: Int = 1 // 0: Email, 1: API Key, 2: Webhook
    var webhookUrl: String = ""
    var secretToken: String = ""

    suspend fun getWorkflows(): List<JSONObject> = withContext(Dispatchers.IO) {
        if (authMode == 2) return@withContext emptyList()
        try {
            val urlString = if (authMode == 1) "$baseUrl/api/v1/workflows" else "$baseUrl/rest/workflows"
            val connection = URL(urlString).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            if (authMode == 1) {
                connection.setRequestProperty("X-N8N-API-KEY", apiKey)
            } else if (cookie.isNotEmpty()) {
                connection.setRequestProperty("Cookie", cookie)
            }
            connection.setRequestProperty("Accept", "application/json")

            if (connection.responseCode in 200..299) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val resultList = mutableListOf<JSONObject>()
                val jsonElement = JSONTokener(response).nextValue()
                if (jsonElement is JSONObject) {
                    val dataArray = jsonElement.optJSONArray("data") ?: JSONArray()
                    for (i in 0 until dataArray.length()) {
                        resultList.add(dataArray.getJSONObject(i))
                    }
                } else if (jsonElement is JSONArray) {
                    for (i in 0 until jsonElement.length()) {
                        resultList.add(jsonElement.getJSONObject(i))
                    }
                }
                return@withContext resultList
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        emptyList()
    }

    suspend fun getExecutions(): List<JSONObject> = withContext(Dispatchers.IO) {
        if (authMode == 2) return@withContext emptyList()
        try {
            val urlString = if (authMode == 1) "$baseUrl/api/v1/executions" else "$baseUrl/rest/executions"
            val connection = URL(urlString).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            if (authMode == 1) {
                connection.setRequestProperty("X-N8N-API-KEY", apiKey)
            } else if (cookie.isNotEmpty()) {
                connection.setRequestProperty("Cookie", cookie)
            }
            connection.setRequestProperty("Accept", "application/json")

            if (connection.responseCode in 200..299) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val resultList = mutableListOf<JSONObject>()
                val jsonElement = JSONTokener(response).nextValue()
                if (jsonElement is JSONObject) {
                    val dataArray = jsonElement.optJSONArray("data") ?: JSONArray()
                    for (i in 0 until dataArray.length()) {
                        resultList.add(dataArray.getJSONObject(i))
                    }
                } else if (jsonElement is JSONArray) {
                    for (i in 0 until jsonElement.length()) {
                        resultList.add(jsonElement.getJSONObject(i))
                    }
                }
                return@withContext resultList
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        emptyList()
    }
}
