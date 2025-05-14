package com.abhi.chatomi.domain.usecase

data class ChatUseCases(
    val getChats: GetChats,
    val sendMessage: SendMessage,
    val retryFailedMessages: RetryFailedMessages,
    val clearChats: ClearChats,
    val observeAllChats: ObserveAllChatsUseCase,
    val getUniqueChatName: GenerateUniqueChatNameUseCase,
    val createNewChat: CreateNewChatUseCase,
    val deleteChat: DeleteChatUseCase,
    val generateBotReply: GenerateBotReplyUseCase,
    val getMessagesForChat: GetMessagesForChatUseCase
)