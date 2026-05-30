package com.baidaidai.rootless_store.domain.notification.usecase

import com.baidaidai.rootless_store.data.notification.gateway.NotificationManagerGatewayImpl
import com.baidaidai.rootless_store.data.notification.repository.NotificationPreferenceRepositoryImpl
import javax.inject.Inject

class PostPluginExitNotificationUseCase @Inject constructor(
    private val notificationManagerGatewayImpl: NotificationManagerGatewayImpl,
    private val notificationPreferenceRepositoryImpl: NotificationPreferenceRepositoryImpl
) {

    suspend operator fun invoke(
        title: String = "Rootless Store",
        message: String = "你有一个插件异常退出，若非本人操作，请及时前往处理",
    ){
        val notificationPreference = notificationPreferenceRepositoryImpl.getOneNotificationPreference()

        if (notificationPreference != null){
            if (notificationPreference.criticalWarning){
                notificationManagerGatewayImpl.pushWarningNotificationOverBark(title,message, apiKey = notificationPreference.apiKey)
            }else{
                notificationManagerGatewayImpl.pushNormalNotificationOverBark(title,message, apiKey = notificationPreference.apiKey)
            }
        }

    }

}