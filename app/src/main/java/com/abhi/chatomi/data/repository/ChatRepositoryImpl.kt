package com.abhi.chatomi.data.repository

import com.abhi.chatomi.data.local.dao.ChatDao
import com.abhi.chatomi.data.local.dao.MessageDao
import com.abhi.chatomi.data.local.entity.ChatEntity
import com.abhi.chatomi.data.local.entity.MessageEntity
import com.abhi.chatomi.data.remote.PieSocketService
import com.abhi.chatomi.domain.model.Chat
import com.abhi.chatomi.domain.model.Message
import com.abhi.chatomi.domain.model.toEntity
import com.abhi.chatomi.domain.repository.ChatRepository
import com.abhi.chatomi.presentation.chat.state.ConnectionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject


class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val messageDao: MessageDao,
    private val pieSocketService: PieSocketService
) : ChatRepository {

    override suspend fun getAllChats(): List<Chat> {
        return chatDao.getAllChats().map { it.toChat() }
    }

    override suspend fun createNewChat(title: String): Chat {
        val newChatId = UUID.randomUUID().toString()
        val newChat = ChatEntity(
            id = newChatId,
            title = title,
            lastMessage = "",
            timestamp = System.currentTimeMillis(),
            unreadCount = 0,
            isOnline = true
        )
        chatDao.insertChat(newChat)
        return newChat.toChat()
    }

    override suspend fun getChatById(chatId: String): Chat? {
        return chatDao.getChatById(chatId)?.toChat()
    }

    override fun observeAllChats(): Flow<List<Chat>> {
        return chatDao.observeAllChats().map { chats ->
            chats.map { it.toChat() }
        }
    }

    override suspend fun deleteChat(chatId: String) {
        chatDao.deleteChat(chatId)
        messageDao.clearMessagesForChat(chatId)
    }


    override fun observeMessagesForChat(chatId: String): Flow<List<Message>> {
        return messageDao.observeMessagesForChat(chatId).map { messages ->
            messages.map { it.toMessage() }
        }
    }

    override suspend fun sendMessage(
        chatId: String,
        message: String,
        isUserMessage: Boolean,
        connectionState: ConnectionState
    ): Boolean {
        var isSend = false;
        try {
            val messageEntity = MessageEntity(
                id = UUID.randomUUID().toString(),
                chatId = chatId,
                content = message,
                timestamp = System.currentTimeMillis(),
                isUserMessage = isUserMessage,
                isSent = false,
                isDelivered = false
            )
            insertMessage(messageEntity.toMessage())

            if (isUserMessage) {
                pieSocketService.sendMessage(messageEntity.toMessage())
                when (connectionState) {
                    is ConnectionState.Connected -> {
                        markMessageAsDelivered(messageEntity.id)
                        isSend = true
                    }
                    else -> {
                        {}
                    }
                }
            }

            updateChatLastMessage(chatId, message)
        } catch (e: Exception) {
            // Message remains with isSent=false for retry later
            throw e
        }
        return isSend
    }


    override suspend fun retryFailedMessages() {
        val failedMessages = messageDao.getFailedMessages()
        failedMessages.forEach { message ->
            try {
                pieSocketService.sendMessage(message.toMessage())
                messageDao.markMessageAsDelivered(message.id)
                updateChatLastMessage(message.chatId, message.content)
            } catch (e: Exception) {
                // Keep as failed for next retry
            }
        }
    }

    override suspend fun generateBotReply(userMessage: String): String {
        return when {
            userMessage.contains("hello", ignoreCase = true) -> "Hello there! How can I help you today?"
            userMessage.contains("help", ignoreCase = true) -> "I'm here to assist you. What do you need?"
            userMessage.contains("?", ignoreCase = true) -> "That's an interesting question. Let me think about it..."
            else -> listOf(
                "I understand.",
                "That's interesting!",
                "Tell me more about that.",
                "How does that make you feel?",
                "I see what you mean."
            ).random()
        }
    }

    override fun generateUniqueChatName(): String {
        val adjectives = listOf("Happy", "Curious", "Mysterious", "Friendly", "Thoughtful")
        val nouns = listOf("Conversation", "Chat", "Dialogue", "Exchange", "Discussion")
        val randomNum = (1..100).random()
        return "${adjectives.random()} ${nouns.random()} #$randomNum"
    }

    override suspend fun clearAllData() {
        chatDao.clearAllChats()
        messageDao.clearAllMessages()
    }

    override suspend fun insertMessage(message: Message) {
        messageDao.insertMessage(message.toEntity())
    }

    override suspend fun getMessagesForChat(chatId: String): List<Message> {
        return messageDao.getMessagesForChat(chatId).map { it.toMessage() }
    }

    override suspend fun markMessageAsDelivered(messageId: String) {
        messageDao.markMessageAsDelivered(messageId)
    }

    private suspend fun sendBotMessage(chatId: String, content: String) {
        val botMessage = Message(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            content = content,
            timestamp = System.currentTimeMillis(),
            isUserMessage = false,
            isSent = true,
            isDelivered = true
        )

        insertMessage(botMessage)
        updateChatLastMessage(chatId, content)
    }

    private suspend fun updateChatLastMessage(chatId: String, message: String) {
        val existingChat = chatDao.getChatById(chatId)

        if (existingChat != null) {
            val updatedChat = existingChat.copy(
                lastMessage = message,
                timestamp = System.currentTimeMillis(),
                unreadCount = if (existingChat.isOnline) 0 else existingChat.unreadCount + 1
            )
            chatDao.insertChat(updatedChat)
        } else {
            val newChat = ChatEntity(
                id = chatId,
                title = "Chat $chatId",
                lastMessage = message,
                timestamp = System.currentTimeMillis(),
                unreadCount = 1,
                isOnline = true
            )
            chatDao.insertChat(newChat)
        }
    }
}