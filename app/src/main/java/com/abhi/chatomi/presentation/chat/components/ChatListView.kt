package com.abhi.chatomi.presentation.chat.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.abhi.chatomi.domain.model.Chat
import com.abhi.chatomi.presentation.chat.state.ConnectionState

@Composable
fun ChatListView(
    chats: List<Chat>,
    connectionState: ConnectionState,
    onChatClick: (Chat) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        ConnectionStatusBar(connectionState = connectionState)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(chats, key = { it.id }) { chat ->
                ChatListItem(
                    chat = chat,
                    onClick = { onChatClick(chat) }
                )
                Divider()
            }
        }
    }
}