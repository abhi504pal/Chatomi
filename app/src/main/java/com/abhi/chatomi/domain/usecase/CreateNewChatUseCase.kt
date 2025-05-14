package com.abhi.chatomi.domain.usecase

import com.abhi.chatomi.domain.model.Chat
import com.abhi.chatomi.domain.repository.ChatRepository
import javax.inject.Inject

class CreateNewChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(title: String = "New Chat"): Chat {
        return chatRepository.createNewChat(title)
    }
}