package com.abhi.chatomi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.abhi.chatomi.domain.model.Message
import java.util.*

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val content: String,
    val timestamp: Long,
    val isUserMessage: Boolean,
    val isSent: Boolean,
    val isDelivered: Boolean
) {
    fun toMessage(): Message {
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
}