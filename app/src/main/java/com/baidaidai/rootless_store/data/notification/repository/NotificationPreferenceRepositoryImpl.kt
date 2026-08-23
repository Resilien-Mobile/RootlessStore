package com.baidaidai.rootless_store.data.notification.repository

import com.baidaidai.rootless_store.data.database.RootlessStoreDatabase
import com.baidaidai.rootless_store.data.notification.database.NotificationPreferenceEntity
import com.baidaidai.rootless_store.data.notification.mapper.NotificationPreferenceMapper.toNotificationPreference
import com.baidaidai.rootless_store.data.notification.mapper.NotificationPreferenceMapper.toNotificationPreferenceEntity
import com.baidaidai.rootless_store.domain.notification.model.NotificationPreference
import javax.inject.Inject

class NotificationPreferenceRepositoryImpl @Inject constructor(
    rootlessStoreDatabase: RootlessStoreDatabase
) {
    private val notificationPreferenceDao = rootlessStoreDatabase.notificationPreferenceDao()

    // Create
    suspend fun insertNotificationPreference(
        notificationPreference: NotificationPreference
    ) {
        val notificationPreferenceEntity = notificationPreference.toNotificationPreferenceEntity()
        notificationPreferenceDao.insertNotificationPreference(notificationPreferenceEntity)
    }

    // Update
    suspend fun updateNotificationPreference(
        apiKey: String,
        notificationTitle: String? = null,
        selfBuiltServer: String? = null,
        criticalWarning: Boolean
    ) {
        notificationPreferenceDao.updateNotificationPreference(
            apiKey = apiKey,
            notificationTitle = notificationTitle,
            selfBuiltServer = selfBuiltServer,
            criticalWarning = criticalWarning,
        )
    }

    // Read
    suspend fun findNotificationPreference(): NotificationPreference? {
        return notificationPreferenceDao.findNotificationPreference()?.toNotificationPreference()
    }

    // Delete
}
