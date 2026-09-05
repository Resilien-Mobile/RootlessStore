package com.baidaidai.rootless_store.application.notification

import com.baidaidai.rootless_store.data.notification.gateway.NotificationGatewayImpl
import javax.inject.Inject

class PostLocalNotificationUseCase @Inject constructor(
    private val notificationGatewayImpl: NotificationGatewayImpl,
) {

    operator fun invoke(){
        notificationGatewayImpl
            .sendLocalNotification(
                title = "你有一个插件已退出",
                message = "若非本人操作，请尽快前往处理"
            )
    }

}