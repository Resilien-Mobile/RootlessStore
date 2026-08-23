package com.baidaidai.rootless_store.domain.notification.usecase

import com.baidaidai.rootless_store.data.notification.repository.NotificationPreferenceRepositoryImpl
import com.baidaidai.rootless_store.domain.notification.model.NotificationPreference
import javax.inject.Inject

class AddNotificationPreferenceUseCase @Inject constructor(
    private val notificationPreferenceRepositoryImpl: NotificationPreferenceRepositoryImpl
) {
    suspend operator fun invoke(
        barkApiKey: String = "",
        notificationTitle: String? = null,
        selfBuiltServer: String? = null,
        warningNotificationEnabled: Boolean = false
    ) {
        val notificationPreference = notificationPreferenceRepositoryImpl.findNotificationPreference()
        if(notificationPreference == null){
            val newNotificationPreference = NotificationPreference(
                apiKey = barkApiKey,
                notificationTitle = notificationTitle,
                selfBuiltServer = selfBuiltServer,
                criticalWarning = warningNotificationEnabled
            )
            notificationPreferenceRepositoryImpl.insertNotificationPreference(newNotificationPreference)
        }else{
            notificationPreferenceRepositoryImpl.updateNotificationPreference(
                apiKey = barkApiKey,
                notificationTitle = notificationTitle,
                selfBuiltServer = selfBuiltServer,
                criticalWarning = warningNotificationEnabled
            )
        }
    }
}
