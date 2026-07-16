package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun WorkflowsScreen() {
    val workflows = listOf(
        WorkflowItem("Sync Customers to CRM", true, "10 mins ago"),
        WorkflowItem("Daily Database Backup", true, "1 hr ago"),
        WorkflowItem("Send Welcome Emails", true, "2 hrs ago"),
        WorkflowItem("Process Invoices", false, "1 day ago"),
        WorkflowItem("Webhook Receiver (Test)", false, "3 days ago")
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Workflows",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add Workflow")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(workflows) { wf ->
                WorkflowCard(wf)
            }
        }
    }
}

@Composable
fun WorkflowCard(wf: WorkflowItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = wf.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (wf.active) Icons.Default.Circle else Icons.Default.PauseCircleFilled,
                        contentDescription = null,
                        tint = if (wf.active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (wf.active) "Active" else "Inactive",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Last run: ${wf.lastRun}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Execute", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Options")
            }
        }
    }
}

data class WorkflowItem(val name: String, val active: Boolean, val lastRun: String)
