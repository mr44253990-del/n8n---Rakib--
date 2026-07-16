package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background

@Composable
fun DashboardScreen(viewModel: N8nViewModel = viewModel()) {
    val workflows by viewModel.workflows.collectAsState()
    val executions by viewModel.executions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    val glassBackground = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
        )
    )

    val activeCount = workflows.count { it.optBoolean("active", false) }
    val totalCount = workflows.size
    val inactiveCount = totalCount - activeCount
    
    val successExecs = executions.count { !it.optBoolean("waitTill") && !it.has("error") }
    val errorExecs = executions.count { it.has("error") }
    val totalExecs = executions.size
    val successRate = if (totalExecs > 0) ((successExecs.toFloat() / totalExecs) * 100).toInt() else 100

    val stats = listOf(
        StatItem("Total Workflows", "$totalCount", Icons.Default.AccountTree),
        StatItem("Active", "$activeCount", Icons.Default.CheckCircle),
        StatItem("Inactive", "$inactiveCount", Icons.Default.PauseCircle),
        StatItem("Executions", "${executions.size}", Icons.Default.PlayCircle),
        StatItem("Success Rate", "$successRate%", Icons.Default.TrendingUp),
        StatItem("Errors", "$errorExecs", Icons.Default.Error)
    )

    Column(modifier = Modifier.fillMaxSize().background(glassBackground).padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { viewModel.refreshData() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (isLoading && workflows.isEmpty() && executions.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(stats) { stat ->
                    StatCard(stat)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Recent Activity",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        if (executions.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("No recent activity found or using Webhook mode.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                executions.take(5).forEach { exec ->
                    val isSuccess = !exec.has("error")
                    val execId = exec.optString("id", "")
                    val workflowId = exec.optString("workflowId", "Unknown")
                    
                    ActivityRow(
                        text = "Execution $execId for workflow $workflowId",
                        time = "Recently",
                        success = isSuccess
                    )
                }
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