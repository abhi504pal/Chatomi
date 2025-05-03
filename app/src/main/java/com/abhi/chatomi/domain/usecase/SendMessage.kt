package com.abhi.chatomi.domain.usecase

import com.abhi.chatomi.domain.repository.ChatRepository
import com.abhi.chatomi.presentation.chat.state.ConnectionState
import javax.inject.Inject

class SendMessage @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(chatId: String, message: String, isUserMessage: Boolean,value: ConnectionState) =
        repository.sendMessage(chatId, message, isUserMessage, value)
}