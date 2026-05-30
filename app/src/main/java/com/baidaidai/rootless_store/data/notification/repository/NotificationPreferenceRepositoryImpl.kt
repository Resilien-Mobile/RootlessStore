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
    private val notificationPreferenceDAO = rootlessStoreDatabase.notificationPreferenceDao()

    // Create
    suspend fun insertOneNotificationPreference(
        notificationPreference: NotificationPreference
    ) {
        val notificationPreferenceEntity = notificationPreference.toNotificationPreferenceEntity()
        notificationPreferenceDAO.insertOneNotificationPreference(notificationPreferenceEntity)
    }

    // Update

    // Read
    suspend fun getOneNotificationPreference(): NotificationPreference? {
        return notificationPreferenceDAO.getOneNotificationPreference()?.toNotificationPreference()
    }

    // Delete
}