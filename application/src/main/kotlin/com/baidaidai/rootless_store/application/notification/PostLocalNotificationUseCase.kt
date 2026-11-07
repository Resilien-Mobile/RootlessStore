package com.baidaidai.rootless_store.application.notification

import com.baidaidai.rootless_store.domain.notification.gateway.NotificationGateway
import javax.inject.Inject

class PostLocalNotificationUseCase @Inject constructor(
    private val notificationGateway: NotificationGateway,
) {

    operator fun invoke(){
        notificationGateway
            .sendLocalNotification(
                title = "你有一个插件已退出",
                message = "若非本人操作，请尽快前往处理"
            )
    }

}