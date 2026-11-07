package com.baidaidai.rootless_store.application.notification

import com.baidaidai.rootless_store.domain.notification.gateway.NotificationGateway
import com.baidaidai.rootless_store.domain.notification.repository.NotificationPreferenceRepository
import javax.inject.Inject

class PostBarkNotificationUseCase @Inject constructor(
    private val notificationGateway: NotificationGateway,
    private val notificationPreferenceRepository: NotificationPreferenceRepository
) {

    suspend operator fun invoke(){
        val notificationPreference = notificationPreferenceRepository.findNotificationPreference()

        if (notificationPreference != null){
            if (notificationPreference.isCriticalWarningEnabled){
                notificationGateway
                    .sendCriticalBarkNotification(
                        title = notificationPreference.notificationTitle,
                        apiKey = notificationPreference.apiKey
                    )
            }else{
                notificationGateway
                    .sendBarkNotification(
                        title = notificationPreference.notificationTitle,
                        apiKey = notificationPreference.apiKey
                    )
            }
        }

    }

}