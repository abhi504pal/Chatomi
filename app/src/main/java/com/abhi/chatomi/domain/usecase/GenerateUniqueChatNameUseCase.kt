package com.abhi.chatomi.domain.usecase

import com.abhi.chatomi.domain.repository.ChatRepository
import javax.inject.Inject

class GenerateUniqueChatNameUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(): String {
        return chatRepository.generateUniqueChatName()
    }
}