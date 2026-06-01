package com.baidaidai.rootless_store.domain.notification.usecase

import com.baidaidai.rootless_store.data.notification.gateway.NotificationManagerGatewayImpl
import com.baidaidai.rootless_store.data.notification.repository.NotificationPreferenceRepositoryImpl
import javax.inject.Inject

class PostPluginExitNotificationUseCase @Inject constructor(
    private val notificationManagerGatewayImpl: NotificationManagerGatewayImpl,
    private val notificationPreferenceRepositoryImpl: NotificationPreferenceRepositoryImpl
) {

    suspend operator fun invoke(){
        val notificationPreference = notificationPreferenceRepositoryImpl.getOneNotificationPreference()

        if (notificationPreference != null){
            if (notificationPreference.criticalWarning){
                notificationManagerGatewayImpl
                    .pushWarningNotificationOverBark(
                        title = notificationPreference.notificationTitle,
                        apiKey = notificationPreference.apiKey
                    )
            }else{
                notificationManagerGatewayImpl
                    .pushNormalNotificationOverBark(
                        title = notificationPreference.notificationTitle,
                        apiKey = notificationPreference.apiKey
                    )
            }
        }

    }

}