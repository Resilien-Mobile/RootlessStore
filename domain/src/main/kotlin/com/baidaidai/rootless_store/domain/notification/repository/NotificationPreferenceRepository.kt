package com.baidaidai.rootless_store.domain.notification.repository

import com.baidaidai.rootless_store.domain.notification.model.NotificationPreference

interface NotificationPreferenceRepository {

    suspend fun addNotificationPreference(
        notificationPreference: NotificationPreference
    )

    suspend fun updateNotificationPreference(
        apiKey: String,
        notificationTitle: String? = null,
        selfBuiltServer: String? = null,
        isCriticalWarningEnabled: Boolean
    )

    suspend fun findNotificationPreference(): NotificationPreference?

}
