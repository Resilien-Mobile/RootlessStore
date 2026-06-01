package com.baidaidai.rootless_store.domain.notification.usecase

import com.baidaidai.rootless_store.data.notification.repository.NotificationPreferenceRepositoryImpl
import com.baidaidai.rootless_store.domain.notification.model.NotificationPreference
import javax.inject.Inject

class AddOneNotificationPreferenceUseCase @Inject constructor(
    private val notificationPreferenceRepositoryImpl: NotificationPreferenceRepositoryImpl
) {
    suspend operator fun invoke(
        barkApiKey: String = "",
        notificationTitle: String? = null,
        selfBuiltServer: String? = null,
        warningNotificationEnabled: Boolean = false
    ) {
        val notificationPreference = notificationPreferenceRepositoryImpl.getOneNotificationPreference()
        if(notificationPreference == null){
            val newNotificationPreference = NotificationPreference(
                apiKey = barkApiKey,
                notificationTitle = notificationTitle,
                selfBuiltServer = selfBuiltServer,
                criticalWarning = warningNotificationEnabled
            )
            notificationPreferenceRepositoryImpl.insertOneNotificationPreference(newNotificationPreference)
        }else{
            notificationPreferenceRepositoryImpl.updateOneNotificationPreference(
                apiKey = barkApiKey,
                notificationTitle = notificationTitle,
                selfBuiltServer = selfBuiltServer,
                criticalWarning = warningNotificationEnabled
            )
        }
    }
}