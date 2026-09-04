package com.baidaidai.rootless_store.data.monitor

import com.baidaidai.rootless_store.data.notification.gateway.NotificationGatewayImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class PluginProcessMonitor @Inject constructor(
    private val notificationGatewayImpl: NotificationGatewayImpl,
//    private val sendPluginExitNotificationUseCase: SendPluginExitNotificationUseCase
) {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    operator fun invoke(process: Process){
        startMonitoring(process)
    }

    operator fun invoke(exitCode: Int){
        if (exitCode != 0) sendUnexpectedExitNotifications()
    }

    private fun startMonitoring(
        process: Process
    ){
        coroutineScope.launch {
            val exitCode = process.waitFor()
            if (exitCode != 0) sendUnexpectedExitNotifications()
        }
    }

    private fun sendUnexpectedExitNotifications(){
//        notificationGatewayImpl
//            .sendLocalNotification(
//                title = "你有一个插件已退出",
//                message = "若非本人操作，请尽快前往处理"
//            )
//        coroutineScope.launch {
//            sendPluginExitNotificationUseCase()
//        }
    }

}
