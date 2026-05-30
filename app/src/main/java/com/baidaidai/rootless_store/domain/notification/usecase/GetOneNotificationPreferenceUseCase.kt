package com.baidaidai.rootless_store.domain.notification.usecase

import com.baidaidai.rootless_store.data.notification.repository.NotificationPreferenceRepositoryImpl
import com.baidaidai.rootless_store.domain.notification.model.NotificationPreference
import javax.inject.Inject

class GetOneNotificationPreferenceUseCase @Inject constructor(
    private val notificationPreferenceRepositoryImpl: NotificationPreferenceRepositoryImpl
) {
    suspend operator fun invoke(): NotificationPreference? =
        notificationPreferenceRepositoryImpl.getOneNotificationPreference()
}
