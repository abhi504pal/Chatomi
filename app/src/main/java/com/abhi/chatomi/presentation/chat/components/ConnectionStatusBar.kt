package com.abhi.chatomi.presentation.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.abhi.chatomi.presentation.chat.state.ConnectionState

@Composable
fun ConnectionStatusBar(connectionState: ConnectionState) {
    val (message, color) = when (connectionState) {
        is ConnectionState.Connected -> "Connected" to MaterialTheme.colorScheme.primary
        is ConnectionState.Disconnected -> "Disconnected" to MaterialTheme.colorScheme.error
        is ConnectionState.Error -> "Error: ${connectionState.message}" to MaterialTheme.colorScheme.error
        ConnectionState.Idle -> "Connecting..." to MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    }

    Text(
        text = message,
        modifier = Modifier
            .fillMaxWidth()
            .background(color.copy(alpha = 0.1f))
            .padding(8.dp),
        color = color,
        style = MaterialTheme.typography.labelSmall,
        textAlign = TextAlign.Center
    )
}