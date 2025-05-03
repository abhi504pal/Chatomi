package com.abhi.chatomi.presentation.chat.state

import com.abhi.chatomi.domain.model.Chat

sealed class ChatState {
    object LOADING : ChatState()
    object SUCCESS : ChatState()
    object ERROR : ChatState()
}
