package com.abhi.chatomi.domain.usecase

import com.abhi.chatomi.domain.model.Message
import com.abhi.chatomi.domain.repository.ChatRepository
import javax.inject.Inject

class GetMessagesForChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(chatId: String): List<Message> {
        return chatRepository.getMessagesForChat(chatId)
    }
}