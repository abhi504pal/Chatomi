package com.abhi.chatomi

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.abhi.chatomi.data.remote.PieSocketService
import com.abhi.chatomi.util.ConnectionObserver
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltAndroidApp
class ChatApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var pieSocketService: PieSocketService

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var connectionObserver: ConnectionObserver

    override fun onCreate() {
        super.onCreate()
        setupNetworkMonitoring()
    }

    private fun setupNetworkMonitoring() {
        connectionObserver = ConnectionObserver(this)
        connectionObserver.observe()
            .onEach { isConnected ->
                if (isConnected) {
                    pieSocketService.connect()
                } else {
                    pieSocketService.disconnect()
                }
            }
            .launchIn(applicationScope)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}