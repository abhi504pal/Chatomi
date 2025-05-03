package com.abhi.chatomi.presentation.chat.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.abhi.chatomi.domain.model.Chat
import com.abhi.chatomi.presentation.chat.viewmodel.ChatViewModel
import com.abhi.chatomi.presentation.chat.state.ChatState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    onChatSelected: (Chat) -> Unit,
    onCreateNewChat: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val chats by viewModel.chats.collectAsState()
    val error by viewModel.error.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateNewChat) {
                Icon(Icons.Default.Add, contentDescription = "New chat")
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("Chats") },
                actions = {
                    IconButton(onClick = { viewModel.clearAllData() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear all")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (uiState) {
                ChatState.LOADING -> FullScreenLoading()
                ChatState.ERROR -> ErrorScreen(
                    message = error,
                    onRetry = { viewModel.loadChats() },
                    showRetry = chats.isEmpty()
                )
                ChatState.SUCCESS -> {
                    if (chats.isEmpty()) {
                        EmptyChatListScreen(onCreateNewChat = onCreateNewChat)
                    } else {
                        ChatListView(
                            chats = chats,
                            connectionState = connectionState,
                            onChatClick = onChatSelected
                        )
                    }
                }
            }
        }
    }
}
