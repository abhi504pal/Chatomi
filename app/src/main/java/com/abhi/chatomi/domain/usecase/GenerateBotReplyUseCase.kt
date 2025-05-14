package com.abhi.chatomi.domain.usecase

import com.abhi.chatomi.domain.repository.ChatRepository
import javax.inject.Inject

class GenerateBotReplyUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(userMessage: String): String {
        return chatRepository.generateBotReply(userMessage)
    }
}