package com.baidaidai.rootless_store.application.notification

import com.baidaidai.rootless_store.data.notification.repository.NotificationPreferenceRepositoryImpl
import com.baidaidai.rootless_store.domain.notification.model.NotificationPreference
import javax.inject.Inject

class EnsureNotificationPreferenceUseCase @Inject constructor(
    private val notificationPreferenceRepositoryImpl: NotificationPreferenceRepositoryImpl
) {
    suspend operator fun invoke(
        barkApiKey: String = "",
        notificationTitle: String? = null,
        selfBuiltServer: String? = null,
        isWarningNotificationEnabled: Boolean = false
    ) {
        val notificationPreference = notificationPreferenceRepositoryImpl.findNotificationPreference()
        if(notificationPreference == null){
            val newNotificationPreference = NotificationPreference(
                apiKey = barkApiKey,
                notificationTitle = notificationTitle,
                selfBuiltServer = selfBuiltServer,
                isCriticalWarningEnabled = isWarningNotificationEnabled
            )
            notificationPreferenceRepositoryImpl.addNotificationPreference(newNotificationPreference)
        }else{
            notificationPreferenceRepositoryImpl.updateNotificationPreference(
                apiKey = barkApiKey,
                notificationTitle = notificationTitle,
                selfBuiltServer = selfBuiltServer,
                isCriticalWarningEnabled = isWarningNotificationEnabled
            )
        }
    }
}
