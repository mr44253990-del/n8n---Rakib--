package com.example.util

import android.util.Log
import com.example.N8nApplication
import com.example.data.ChatMessageEntity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d("FCM", "Refreshed token: $token")
        com.example.data.PrefManager.fcmToken = token
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FCM", "Message data payload: ${remoteMessage.data}")
            val message = remoteMessage.data["message"] ?: remoteMessage.data["text"] ?: remoteMessage.data.toString()
            saveAndShowMessage(message)
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d("FCM", "Message Notification Body: ${it.body}")
            if (remoteMessage.data.isEmpty() && it.body != null) {
               saveAndShowMessage(it.body!!)
            }
        }
    }
    
    private fun saveAndShowMessage(message: String) {
        val app = application as N8nApplication
        CoroutineScope(Dispatchers.IO).launch {
             app.database.chatDao().insertMessage(
                 ChatMessageEntity(sender = "System", text = message, isSystem = true)
             )
        }
        if (!AppLifecycleTracker.isForeground) {
             NotificationHelper.showNotification(applicationContext, "n8n AI Response", message)
        }
    }
}
