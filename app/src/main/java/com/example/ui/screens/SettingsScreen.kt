package com.example.ui.screens

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.N8nApiClient

import android.provider.Settings as AndroidSettings
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigateToFeature: (String) -> Unit = {}, onLogout: () -> Unit = {}) {
    val context = LocalContext.current
    val deviceId = remember { AndroidSettings.Secure.getString(context.contentResolver, AndroidSettings.Secure.ANDROID_ID) ?: "default_device" }
    var webhookUrlText by remember { mutableStateOf(com.example.data.PrefManager.webhookUrl) }
    var showWebhookSave by remember { mutableStateOf(false) }

    val glassBackground = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
        )
    )

    Column(modifier = Modifier.fillMaxSize().background(glassBackground).padding(16.dp).verticalScroll(rememberScrollState())) {
        Text(
            text = "Settings & Features",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Background Push Notifications", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("To send a push notification from n8n to this device, send an HTTP POST request to:", style = MaterialTheme.typography.bodySmall)
                Text("https://ntfy.sh/n8n_aistudio_$deviceId", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 8.dp))
                Text("Body: { \"message\": \"Your text here\" }", style = MaterialTheme.typography.bodySmall)
            }
        }
        
        if (N8nApiClient.authMode == 2) {
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Webhook URL Configuration", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = webhookUrlText,
                        onValueChange = { 
                            webhookUrlText = it 
                            showWebhookSave = it != com.example.data.PrefManager.webhookUrl
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Webhook URL") }
                    )
                    if (showWebhookSave) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { 
                            com.example.data.PrefManager.webhookUrl = webhookUrlText
                            N8nApiClient.webhookUrl = webhookUrlText
                            showWebhookSave = false
                        }) {
                            Text("Save Webhook URL")
                        }
                    }
                }
            }
        }

        SettingItem(icon = Icons.Default.Person, title = "Account", onClick = { onNavigateToFeature("Account") })
        SettingItem(icon = Icons.Default.Palette, title = "Theme & Appearance", onClick = { onNavigateToFeature("Theme & Appearance") })
        SettingItem(icon = Icons.Default.Notifications, title = "Push Notifications", onClick = { onNavigateToFeature("Push Notifications") })
        SettingItem(icon = Icons.Default.Sync, title = "Background Processing", onClick = { onNavigateToFeature("Background Processing") })
        
        if (N8nApiClient.authMode == 1) {
            SettingItem(icon = Icons.Default.Security, title = "Credentials Manager", onClick = { onNavigateToFeature("Credentials Manager") })
            SettingItem(icon = Icons.Default.Storage, title = "Storage & Cache", onClick = { onNavigateToFeature("Storage & Cache") })
            SettingItem(icon = Icons.Default.Monitor, title = "Activity Monitor", onClick = { onNavigateToFeature("Activity Monitor") })
            SettingItem(icon = Icons.Default.ListAlt, title = "Execution Details", onClick = { onNavigateToFeature("Execution Details") })
            SettingItem(icon = Icons.Default.Edit, title = "Workflow Editor", onClick = { onNavigateToFeature("Workflow Editor") })
            SettingItem(icon = Icons.Default.Folder, title = "Files", onClick = { onNavigateToFeature("Files") })
            SettingItem(icon = Icons.Default.Notes, title = "Logs", onClick = { onNavigateToFeature("Logs") })
            SettingItem(icon = Icons.Default.Star, title = "Smart Features", onClick = { onNavigateToFeature("Smart Features") })
            SettingItem(icon = Icons.Default.AdminPanelSettings, title = "Admin Panel (Enterprise)", onClick = { onNavigateToFeature("Admin Panel") })
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Logout")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Connected to: ${if (N8nApiClient.authMode == 2) N8nApiClient.webhookUrl else N8nApiClient.baseUrl}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun SettingItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, onClick: () -> Unit = {}) {
    Surface(
        onClick = onClick,
        color = androidx.compose.ui.graphics.Color.Transparent
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
