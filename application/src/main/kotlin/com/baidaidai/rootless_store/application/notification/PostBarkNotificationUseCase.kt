package com.baidaidai.rootless_store.application.notification

import com.baidaidai.rootless_store.data.notification.gateway.NotificationGatewayImpl
import com.baidaidai.rootless_store.data.notification.repository.NotificationPreferenceRepositoryImpl
import javax.inject.Inject

class PostBarkNotificationUseCase @Inject constructor(
    private val notificationGatewayImpl: NotificationGatewayImpl,
    private val notificationPreferenceRepositoryImpl: NotificationPreferenceRepositoryImpl
) {

    suspend operator fun invoke(){
        val notificationPreference = notificationPreferenceRepositoryImpl.findNotificationPreference()

        if (notificationPreference != null){
            if (notificationPreference.isCriticalWarningEnabled){
                notificationGatewayImpl
                    .sendCriticalBarkNotification(
                        title = notificationPreference.notificationTitle,
                        apiKey = notificationPreference.apiKey
                    )
            }else{
                notificationGatewayImpl
                    .sendBarkNotification(
                        title = notificationPreference.notificationTitle,
                        apiKey = notificationPreference.apiKey
                    )
            }
        }

    }

}