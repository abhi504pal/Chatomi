package com.abhi.chatomi.domain.usecase

data class ChatUseCases(
    val getChats: GetChats,
    val sendMessage: SendMessage,
    val retryFailedMessages: RetryFailedMessages,
    val clearChats: ClearChats
)