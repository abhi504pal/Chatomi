package com.abhi.chatomi.presentation.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhi.chatomi.data.remote.PieSocketService
import com.abhi.chatomi.domain.model.Chat
import com.abhi.chatomi.domain.model.Message
import com.abhi.chatomi.domain.repository.ChatRepository
import com.abhi.chatomi.presentation.chat.state.ChatState
import com.abhi.chatomi.presentation.chat.state.ConnectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val pieSocketService: PieSocketService
) : ViewModel() {

    private val _state = MutableStateFlow<ChatState>(ChatState.LOADING)
    val state: StateFlow<ChatState> = _state.asStateFlow()

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats.asStateFlow()

    private val _selectedChat = MutableStateFlow<Chat?>(null)
    val selectedChat: StateFlow<Chat?> = _selectedChat.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Idle)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    init {

        loadChats()
        setupSocketConnection()
        observeChatUpdates()
    }

    private fun setupSocketConnection() {
        viewModelScope.launch {
            pieSocketService.connect()

            pieSocketService.connectionState.collect { state ->
                when (state) {
                    is PieSocketService.ConnectionState.Connected -> {
                        _connectionState.value = ConnectionState.Connected
                        retryFailedMessages()
                    }
                    is PieSocketService.ConnectionState.Disconnected -> {
                        _connectionState.value = ConnectionState.Disconnected
                    }
                    is PieSocketService.ConnectionState.Error -> {
                        _connectionState.value = ConnectionState.Error(state.message)
                    }
                }
            }
        }
    }

    private fun observeChatUpdates() {
        viewModelScope.launch {
            chatRepository.observeAllChats().collect { chatList ->
                _chats.value = chatList
                _state.value = ChatState.SUCCESS
            }
        }
    }

    fun loadChats() {
        viewModelScope.launch {
            chatRepository.clearAllData()
            _state.value = ChatState.LOADING
            try {
                _chats.value = chatRepository.getAllChats()
                _state.value = ChatState.SUCCESS
            } catch (e: Exception) {
                _error.value = e.message
                _state.value = ChatState.ERROR
            }
        }
    }

    fun createNewChat() {
        viewModelScope.launch {
            try {
                val chatName = chatRepository.generateUniqueChatName()
                val newChat = chatRepository.createNewChat(chatName)
                _selectedChat.value = newChat
                _messages.value = emptyList()

                // Send welcome message from bot
                sendBotMessage(newChat.id, "Hello! I'm your chat assistant. How can I help you?")
            } catch (e: Exception) {
                _error.value = "Failed to create chat: ${e.message}"
                _state.value = ChatState.ERROR
            }
        }
    }

    fun selectChat(chat: Chat) {
        viewModelScope.launch {
            _selectedChat.value = chat
            loadMessagesForChat(chat.id)
        }
    }

    private fun loadMessagesForChat(chatId: String) {
        viewModelScope.launch {
            try {
                _messages.value = chatRepository.getMessagesForChat(chatId)
            } catch (e: Exception) {
                _error.value = "Failed to load messages: ${e.message}"
                _state.value = ChatState.ERROR
            }
        }
    }

    private suspend fun sendBotMessage(chatId: String, content: String) {
        chatRepository.sendMessage(chatId, content, false, connectionState.value)
        loadMessagesForChat(chatId)
    }

    fun sendMessage(message: String) {
        val chatId = _selectedChat.value?.id ?: return
        viewModelScope.launch {
            try {
                // Send user message
                if(chatRepository.sendMessage(chatId, message, true,connectionState.value)){
                    val botReply = chatRepository.generateBotReply(message)
                    sendBotMessage(chatId, botReply)
                }

                loadMessagesForChat(chatId)

            } catch (e: Exception) {
                _error.value = e.message
                _state.value = ChatState.ERROR
            }
        }
    }

    fun retryFailedMessages() {
        viewModelScope.launch {
            try {
                chatRepository.retryFailedMessages()
                _selectedChat.value?.id?.let { loadMessagesForChat(it) }
            } catch (e: Exception) {
                _error.value = "Failed to retry messages: ${e.message}"
                _state.value = ChatState.ERROR
            }
        }
    }

    fun clearError() {
        _error.value = null
        _state.value = ChatState.SUCCESS
    }

    fun deleteCurrentChat() {
        val chatId = _selectedChat.value?.id ?: return
        viewModelScope.launch {
            try {
                chatRepository.deleteChat(chatId)
                _selectedChat.value = null
                _messages.value = emptyList()
                loadChats()
            } catch (e: Exception) {
                _error.value = "Failed to delete chat: ${e.message}"
                _state.value = ChatState.ERROR
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            try {
                chatRepository.clearAllData()
                _selectedChat.value = null
                _messages.value = emptyList()
                loadChats()
            } catch (e: Exception) {
                _error.value = "Failed to clear data: ${e.message}"
                _state.value = ChatState.ERROR
            }
        }
    }

    override fun onCleared() {
        CoroutineScope(Dispatchers.IO).async {
            chatRepository.clearAllData()
            pieSocketService.disconnect()
        }
        super.onCleared()
    }
}