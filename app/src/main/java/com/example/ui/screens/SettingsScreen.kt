package com.example.ui.screens

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text(
            text = "Settings & Features",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        SettingItem(icon = Icons.Default.Person, title = "Account")
        SettingItem(icon = Icons.Default.Palette, title = "Theme & Appearance")
        SettingItem(icon = Icons.Default.Notifications, title = "Push Notifications")
        SettingItem(icon = Icons.Default.Sync, title = "Background Processing")
        SettingItem(icon = Icons.Default.Security, title = "Credentials Manager")
        SettingItem(icon = Icons.Default.Storage, title = "Storage & Cache")
        SettingItem(icon = Icons.Default.Monitor, title = "Activity Monitor")
        SettingItem(icon = Icons.Default.ListAlt, title = "Execution Details")
        SettingItem(icon = Icons.Default.Edit, title = "Workflow Editor")
        SettingItem(icon = Icons.Default.Folder, title = "Files")
        SettingItem(icon = Icons.Default.Notes, title = "Logs")
        SettingItem(icon = Icons.Default.Star, title = "Smart Features")
        SettingItem(icon = Icons.Default.AdminPanelSettings, title = "Admin Panel (Enterprise)")
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { /* TODO Logout */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Logout")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Version 1.0.0 | API: https://your-domain.com/api/v1",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun SettingItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
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
