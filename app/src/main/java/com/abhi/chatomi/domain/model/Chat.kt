package com.abhi.chatomi.domain.model

import java.util.UUID

data class Chat(
    val id: String,
    val title: String,
    val lastMessage: String,
    val timestamp: Long,
    val unreadCount: Int,
    val isOnline: Boolean
)