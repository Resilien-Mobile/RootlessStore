package com.baidaidai.rootless_store.data.monitor

import com.baidaidai.rootless_store.data.notification.gateway.NotificationManagerGatewayImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProcessMonitor @Inject constructor(
    private val notificationManagerGatewayImpl: NotificationManagerGatewayImpl
) {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    operator fun invoke(process: Process){
        onRunning(process)
    }

    operator fun invoke(exitCode: Int){
        if (exitCode != 0) onProcessKilled()
    }

    private fun onRunning(
        process: Process
    ){
        coroutineScope.launch {
            val exitCode = process.waitFor()
            if (exitCode != 0) onProcessKilled()
        }
    }

    private fun onProcessKilled(){
        notificationManagerGatewayImpl
            .pushDefaultNotification(
                title = "你有一个插件已退出",
                message = "若非本人操作，请尽快前往处理"
            )
    }

}