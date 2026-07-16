package com.example.util

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class NtfyService : Service() {
    private var serviceJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    
    companion object {
        private const val CHANNEL_ID = "ntfy_service_channel"
        var isRunning = false
    }

    override fun onBind(intent: Intent?): IBinder? = null

    @SuppressLint("HardwareIds")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning) {
            isRunning = true
            createNotificationChannel()
            val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("n8n Background Sync Active")
                .setContentText("Listening for real-time webhook updates...")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()

            startForeground(1, notification)
            
            val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID) ?: "default_device"
            val topic = "n8n_aistudio_$deviceId"

            serviceJob = scope.launch {
                while (isActive) {
                    try {
                        val url = URL("https://ntfy.sh/$topic/json")
                        val connection = url.openConnection() as HttpURLConnection
                        connection.requestMethod = "GET"
                        connection.readTimeout = 0 // Infinite timeout for SSE
                        
                        val reader = BufferedReader(InputStreamReader(connection.inputStream))
                        var line: String? = null
                        while (isActive) {
                            line = reader.readLine()
                            if (line == null) break
                            if (line.isNotEmpty()) {
                                try {
                                    val json = JSONObject(line)
                                    if (json.optString("event") == "message") {
                                        val message = json.optString("message")
                                        if (!AppLifecycleTracker.isForeground) {
                                            NotificationHelper.showNotification(applicationContext, "n8n AI Response", message)
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                        reader.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        delay(5000) // Retry after 5s if disconnected
                    }
                }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob?.cancel()
        isRunning = false
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "n8n Background Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}
