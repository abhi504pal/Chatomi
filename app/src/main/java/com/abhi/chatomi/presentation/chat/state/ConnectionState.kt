package com.abhi.chatomi.presentation.chat.state

sealed class ConnectionState {
    object Connected : ConnectionState()
    object Disconnected : ConnectionState()
    data class Error(val message: String?) : ConnectionState()
    object Idle : ConnectionState()
}