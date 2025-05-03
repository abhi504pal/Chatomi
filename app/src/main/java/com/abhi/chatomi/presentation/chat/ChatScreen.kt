package com.abhi.chatomi.presentation.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.abhi.chatomi.domain.model.Chat
import com.abhi.chatomi.presentation.chat.components.ChatListScreen
import com.abhi.chatomi.presentation.chat.components.MessagesScreen
import com.abhi.chatomi.presentation.chat.viewmodel.ChatViewModel


@Composable
fun ChatScreen() {
    val viewModel: ChatViewModel = hiltViewModel()
    var selectedChat by remember { mutableStateOf<Chat?>(null) }

    if (selectedChat == null) {
        ChatListScreen(
            onChatSelected = { chat ->
                selectedChat = chat
                viewModel.selectChat(chat)
            },
            onCreateNewChat = {
                viewModel.createNewChat()
                selectedChat = viewModel.selectedChat.value
            }
        )
    } else {
        MessagesScreen(
            chat = selectedChat!!,
            onBack = { selectedChat = null },
            onDeleteChat = { viewModel.deleteCurrentChat() }
        )
    }
}