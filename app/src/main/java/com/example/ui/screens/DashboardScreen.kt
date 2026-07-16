package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen() {
    val stats = listOf(
        StatItem("Active Workflows", "12", Icons.Default.CheckCircle),
        StatItem("Inactive", "4", Icons.Default.PauseCircle),
        StatItem("Failed Execs", "3", Icons.Default.Error),
        StatItem("Running", "1", Icons.Default.PlayCircle),
        StatItem("Success Rate", "98.5%", Icons.Default.TrendingUp),
        StatItem("Uptime", "99.9%", Icons.Default.Timer)
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(stats) { stat ->
                StatCard(stat)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Recent Activity",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                ActivityRow("Webhook triggered - Sync Customers", "2 mins ago", true)
                ActivityRow("Failed execution - API limit", "15 mins ago", false)
                ActivityRow("Workflow started - Daily Backup", "1 hr ago", true)
            }
        }
    }
}

@Composable
fun StatCard(stat: StatItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = stat.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stat.value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = stat.label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ActivityRow(text: String, time: String, success: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (success) Icons.Default.CheckCircle else Icons.Default.Error,
            contentDescription = null,
            tint = if (success) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = text, style = MaterialTheme.typography.bodyMedium)
            Text(text = time, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

data class StatItem(val label: String, val value: String, val icon: ImageVector)
