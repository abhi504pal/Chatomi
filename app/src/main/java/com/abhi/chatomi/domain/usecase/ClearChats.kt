package com.abhi.chatomi.domain.usecase

import com.abhi.chatomi.domain.repository.ChatRepository
import javax.inject.Inject

class ClearChats @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke() = repository.clearAllData()
}