package com.baidaidai.rootless_store.domain.notification.usecase

import com.baidaidai.rootless_store.data.notification.database.NotificationPreferenceEntity
import com.baidaidai.rootless_store.data.notification.repository.NotificationPreferenceRepositoryImpl
import com.baidaidai.rootless_store.domain.notification.model.NotificationPreference
import javax.inject.Inject

class AddOneNotificationPreferenceUseCase @Inject constructor(
    private val notificationPreferenceRepositoryImpl: NotificationPreferenceRepositoryImpl
) {
    suspend operator fun invoke(
        barkApiKey: String = "",
        warningNotificationEnabled: Boolean = false
    ) {
        val notificationPreference = NotificationPreference(
            apiKey = barkApiKey,
            criticalWarning = warningNotificationEnabled
        )
        notificationPreferenceRepositoryImpl.insertOneNotificationPreference(notificationPreference)
    }
}