package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf(
        ChatMessage("System", "Webhook AI mode active. You can send messages to your n8n workflow here.", true),
    ) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("AI Webhook Chat") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
            )
        )
        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(msg)
            }
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* TODO attach */ }) {
                Icon(Icons.Default.AttachFile, contentDescription = "Attach File")
            }
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Message via Webhook...") },
                shape = RoundedCornerShape(24.dp)
            )
            IconButton(onClick = { 
                if (messageText.isNotBlank()) {
                    messages.add(ChatMessage("User", messageText, false))
                    messageText = ""
                    // Simulate webhook reply
                    messages.add(ChatMessage("System", "Webhook triggered successfully.", true))
                }
            }) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage) {
    val isSystem = msg.isSystem
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isSystem) Arrangement.Start else Arrangement.End
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isSystem) 4.dp else 16.dp,
                bottomEnd = if (isSystem) 16.dp else 4.dp
            ),
            color = if (isSystem) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = msg.text,
                modifier = Modifier.padding(12.dp),
                color = if (isSystem) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

data class ChatMessage(val sender: String, val text: String, val isSystem: Boolean)
