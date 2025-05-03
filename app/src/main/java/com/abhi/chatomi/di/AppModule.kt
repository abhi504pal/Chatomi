// com.abhi.chatomi.di.AppModule.kt
package com.abhi.chatomi.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.abhi.chatomi.data.local.ChatDatabase
import com.abhi.chatomi.data.remote.PieSocketService
import com.abhi.chatomi.data.repository.ChatRepositoryImpl
import com.abhi.chatomi.domain.repository.ChatRepository
import com.abhi.chatomi.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePieSocketService(): PieSocketService {
        return PieSocketService()
    }

    @Provides
    @Singleton
    fun provideChatDatabase(@ApplicationContext context: Context): ChatDatabase {
        return Room.databaseBuilder(
            context,
            ChatDatabase::class.java,
            ChatDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        db: ChatDatabase,
        pieSocketService: PieSocketService
    ): ChatRepository {
        return ChatRepositoryImpl(db.chatDao(), db.messageDao(), pieSocketService)
    }

    @Provides
    @Singleton
    fun provideChatUseCases(repository: ChatRepository): ChatUseCases {
        return ChatUseCases(
            getChats = GetChats(repository),
            sendMessage = SendMessage(repository),
            retryFailedMessages = RetryFailedMessages(repository),
            clearChats = ClearChats(repository)
        )
    }

    @Provides
    @Singleton
    fun provideApplicationContext(application: Application): Context {
        return application.applicationContext
    }
}