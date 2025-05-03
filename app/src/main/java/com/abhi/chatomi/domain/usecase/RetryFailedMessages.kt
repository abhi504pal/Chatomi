package com.abhi.chatomi.domain.usecase

import com.abhi.chatomi.domain.repository.ChatRepository
import javax.inject.Inject

class RetryFailedMessages @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke() = repository.retryFailedMessages()
}