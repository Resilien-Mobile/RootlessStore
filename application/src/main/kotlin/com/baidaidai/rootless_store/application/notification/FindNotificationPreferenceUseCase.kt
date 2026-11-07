package com.baidaidai.rootless_store.application.notification

import com.baidaidai.rootless_store.domain.notification.repository.NotificationPreferenceRepository
import com.baidaidai.rootless_store.domain.notification.model.NotificationPreference
import javax.inject.Inject

class FindNotificationPreferenceUseCase @Inject constructor(
    private val notificationPreferenceRepository: NotificationPreferenceRepository
) {
    suspend operator fun invoke(): NotificationPreference? =
        notificationPreferenceRepository.findNotificationPreference()
}
