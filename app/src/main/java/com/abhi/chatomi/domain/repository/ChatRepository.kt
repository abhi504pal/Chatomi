package com.abhi.chatomi.domain.repository

import com.abhi.chatomi.domain.model.Chat
import com.abhi.chatomi.domain.model.Message
import com.abhi.chatomi.presentation.chat.state.ConnectionState
import kotlinx.coroutines.flow.Flow


interface ChatRepository {
    suspend fun getAllChats(): List<Chat>
    fun observeAllChats(): Flow<List<Chat>>
    suspend fun createNewChat(title: String = "New Chat"): Chat
    suspend fun getChatById(chatId: String): Chat?
    suspend fun deleteChat(chatId: String)

    suspend fun getMessagesForChat(chatId: String): List<Message>
    fun observeMessagesForChat(chatId: String): Flow<List<Message>>
    suspend fun sendMessage(
        chatId: String,
        message: String,
        isUserMessage: Boolean,
        value: ConnectionState
    ) : Boolean
    suspend fun retryFailedMessages()

    suspend fun clearAllData()

    suspend fun generateBotReply(userMessage: String): String
    fun generateUniqueChatName(): String

    suspend fun insertMessage(message: Message)
    suspend fun markMessageAsDelivered(messageId: String)
}