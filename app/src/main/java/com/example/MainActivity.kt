package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.ui.theme.MyApplicationTheme
import com.example.util.AppLifecycleTracker

class MainActivity : ComponentActivity() {
  private val requestPermissionLauncher = registerForActivityResult(
      ActivityResultContracts.RequestPermission()
  ) { isGranted: Boolean ->
      // Permission result
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleTracker)
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    com.google.firebase.messaging.FirebaseMessaging.getInstance().token
        .addOnCompleteListener { task ->
            if (!task.isSuccessful) return@addOnCompleteListener
            val token = task.result
            android.util.Log.d("FCM", token)
            com.example.data.PrefManager.fcmToken = token
        }

    enableEdgeToEdge()
    setContent {
      val authMode = com.example.data.PrefManager.authMode
      if (authMode != -1) {
          com.example.data.N8nApiClient.authMode = authMode
          com.example.data.N8nApiClient.baseUrl = com.example.data.PrefManager.baseUrl
          com.example.data.N8nApiClient.apiKey = com.example.data.PrefManager.apiKey
          com.example.data.N8nApiClient.webhookUrl = com.example.data.PrefManager.webhookUrl
      }
      
      val startDest = if (authMode == 1) com.example.DashboardRoute else if (authMode == 2) com.example.ChatRoute else com.example.LoginRoute

      MyApplicationTheme {
        MainNavigation(startDestination = startDest)
      }
    }
  }
}

