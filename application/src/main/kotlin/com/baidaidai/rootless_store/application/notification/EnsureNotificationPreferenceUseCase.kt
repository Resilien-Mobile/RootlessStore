package com.baidaidai.rootless_store.application.notification

import com.baidaidai.rootless_store.domain.notification.repository.NotificationPreferenceRepository
import com.baidaidai.rootless_store.domain.notification.model.NotificationPreference
import javax.inject.Inject

class EnsureNotificationPreferenceUseCase @Inject constructor(
    private val notificationPreferenceRepository: NotificationPreferenceRepository
) {
    suspend operator fun invoke(
        barkApiKey: String = "",
        notificationTitle: String? = null,
        selfBuiltServer: String? = null,
        isWarningNotificationEnabled: Boolean = false
    ) {
        val notificationPreference = notificationPreferenceRepository.findNotificationPreference()
        if(notificationPreference == null){
            val newNotificationPreference = NotificationPreference(
                apiKey = barkApiKey,
                notificationTitle = notificationTitle,
                selfBuiltServer = selfBuiltServer,
                isCriticalWarningEnabled = isWarningNotificationEnabled
            )
            notificationPreferenceRepository.addNotificationPreference(newNotificationPreference)
        }else{
            notificationPreferenceRepository.updateNotificationPreference(
                apiKey = barkApiKey,
                notificationTitle = notificationTitle,
                selfBuiltServer = selfBuiltServer,
                isCriticalWarningEnabled = isWarningNotificationEnabled
            )
        }
    }
}
