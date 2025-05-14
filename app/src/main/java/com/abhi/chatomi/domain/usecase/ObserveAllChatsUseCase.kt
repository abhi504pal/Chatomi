package com.abhi.chatomi.domain.usecase

import com.abhi.chatomi.domain.model.Chat
import com.abhi.chatomi.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAllChatsUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(): Flow<List<Chat>> {
        return chatRepository.observeAllChats()
    }
}