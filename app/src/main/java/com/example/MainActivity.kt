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

    try {
        val intent = android.content.Intent(this, com.example.util.NtfyService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    } catch (e: Exception) {
        e.printStackTrace()
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

