package com.abhi.chatomi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.abhi.chatomi.data.local.dao.ChatDao
import com.abhi.chatomi.data.local.dao.MessageDao
import com.abhi.chatomi.data.local.entity.ChatEntity
import com.abhi.chatomi.data.local.entity.MessageEntity

@Database(
    entities = [ChatEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao

    companion object {
        const val DATABASE_NAME = "chatomi_db"
    }
}