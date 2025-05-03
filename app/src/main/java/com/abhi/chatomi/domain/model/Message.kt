package com.abhi.chatomi.domain.model

import com.abhi.chatomi.data.local.entity.MessageEntity
import java.util.UUID

data class Message(
    val id: String,
    val chatId: String,
    val content: String,
    val timestamp: Long,
    val isUserMessage: Boolean,
    val isSent: Boolean,
    val isDelivered: Boolean
)

fun MessageEntity.toMessage(): Message {
    return Message(
        id = id,
        chatId = chatId,
        content = content,
        timestamp = timestamp,
        isUserMessage = isUserMessage,
        isSent = isSent,
        isDelivered = isDelivered
    )
}

fun Message.toEntity(): MessageEntity {
    return MessageEntity(
        id = id,
        chatId = chatId,
        content = content,
        timestamp = timestamp,
        isUserMessage = isUserMessage,
        isSent = isSent,
        isDelivered = isDelivered
    )
}