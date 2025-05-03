package com.abhi.chatomi.data.remote

import android.util.Log
import com.abhi.chatomi.BuildConfig.PIE_SOCKET_API_KEY
import com.abhi.chatomi.BuildConfig.PIE_SOCKET_CLUSTER_ID
import com.abhi.chatomi.domain.model.Message
import com.piesocket.channels.BuildConfig
import com.piesocket.channels.Channel
import com.piesocket.channels.PieSocket
import com.piesocket.channels.misc.PieSocketEvent
import com.piesocket.channels.misc.PieSocketEventListener
import com.piesocket.channels.misc.PieSocketOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PieSocketService @Inject constructor() {
    private val TAG = "PieSocketService"

    private var pieSocket: PieSocket? = null
    private var channel: Channel? = null

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    private val _connectionState = MutableSharedFlow<ConnectionState>(replay = 1)
    private val _messages = MutableSharedFlow<Message>()

    val connectionState: SharedFlow<ConnectionState> = _connectionState
    val messages: SharedFlow<Message> = _messages

    fun connect() {
        Log.d(TAG, "Initializing socket connection")
        val options = PieSocketOptions().apply {
            clusterId = PIE_SOCKET_CLUSTER_ID
            this.apiKey = PIE_SOCKET_API_KEY
            notifySelf
        }

        pieSocket = PieSocket(options)
        channel = pieSocket?.join("channelId")

        val listener = object : PieSocketEventListener() {
            override fun handleEvent(event: PieSocketEvent?) {
                Log.d(TAG, "Received event: ${event?.event}, data: ${event?.data}")
                when (event?.event) {
                    "system:connected" -> {
                        Log.i(TAG, "Socket connected successfully")
                        emitConnectionState(ConnectionState.Connected)
                    }
                    "system:disconnected" -> {
                        Log.w(TAG, "Socket disconnected")
                        emitConnectionState(ConnectionState.Disconnected)
                    }
                    "system:error" -> {
                        Log.e(TAG, "Socket error occurred: ${event.data}")
                        emitConnectionState(ConnectionState.Error(event.data.toString()))
                    }
                    "message" -> {
                        Log.d(TAG, "New message received from socket")
                        try {
                            val json = JSONObject(event.data.toString())
                            val message = Message(
                                id = json.getString("id"),
                                chatId = json.getString("chatId"),
                                content = json.getString("content"),
                                timestamp = json.getLong("timestamp"),
                                isUserMessage = json.getBoolean("isUserMessage"),
                                isSent = true,
                                isDelivered = true
                            )
                            Log.d(TAG, "Parsed message: $message")
                            emitMessage(message)
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to parse incoming message", e)
                        }
                    }
                    else -> Log.d(TAG, "Unhandled event: ${event?.event}")
                }
            }
        }

        channel?.apply {
            listen("system:connected", listener)
            listen("system:disconnected", listener)
            listen("system:error", listener)
            listen("message", listener)
        }

        Log.d(TAG, "Listeners registered for system and message events")
    }

    private fun emitConnectionState(state: ConnectionState) {
        Log.d(TAG, "Emitting connection state: $state")
        serviceScope.launch {
            _connectionState.emit(state)
        }
    }

    private fun emitMessage(message: Message) {
        Log.d(TAG, "Emitting message to flow: $message")
        serviceScope.launch {
            _messages.emit(message)
        }
    }

    fun sendMessage(message: Message) {
        try {
            Log.d(TAG, "Preparing to send message: $message")
            val json = JSONObject().apply {
                put("id", message.id)
                put("chatId", message.chatId)
                put("content", message.content)
                put("timestamp", message.timestamp)
                put("isUserMessage", message.isUserMessage)
            }

            val event = PieSocketEvent("message").apply {
                data = json.toString()
            }

            channel?.publish(event)
            Log.i(TAG, "Message sent successfully: $message")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send message through socket", e)
            throw e
        }
    }

    fun disconnect() {
        Log.d(TAG, "Disconnecting socket and clearing references")
        channel?.disconnect()
        channel = null
        pieSocket = null
        Log.d(TAG, "Socket disconnected successfully")
    }

    sealed class ConnectionState {
        object Connected : ConnectionState() {
            override fun toString() = "Connected"
        }

        object Disconnected : ConnectionState() {
            override fun toString() = "Disconnected"
        }

        data class Error(val message: String) : ConnectionState() {
            override fun toString() = "Error(message=$message)"
        }
    }
}