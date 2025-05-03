package com.abhi.chatomi.presentation.chat.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


@Composable
fun ErrorMessage(
    message: String,
    onDismiss: () -> Unit
) {
    var showError by remember { mutableStateOf(true) }

    if (showError) {
        AlertDialog(
            onDismissRequest = {
                showError = false
                onDismiss()
            },
            title = { Text("Error") },
            text = { Text(message) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showError = false
                        onDismiss()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}