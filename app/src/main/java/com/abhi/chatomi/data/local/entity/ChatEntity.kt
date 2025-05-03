package com.abhi.chatomi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.abhi.chatomi.domain.model.Chat


@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: String,
    val title: String,
    val lastMessage: String,
    val timestamp: Long,
    val unreadCount: Int,
    val isOnline: Boolean
) {
    fun toChat(): Chat {
        return Chat(
            id = id,
            title = title,
            lastMessage = lastMessage,
            timestamp = timestamp,
            unreadCount = unreadCount,
            isOnline = isOnline
        )
    }
}