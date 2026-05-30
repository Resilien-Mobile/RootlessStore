package com.baidaidai.rootless_store.data.notification.mapper

import com.baidaidai.rootless_store.data.notification.database.NotificationPreferenceEntity
import com.baidaidai.rootless_store.domain.notification.model.NotificationPreference

object NotificationPreferenceMapper {

    fun NotificationPreferenceEntity.toNotificationPreference(): NotificationPreference {
        return NotificationPreference(
            apiKey = apiKey,
            criticalWarning = criticalWarning
        )
    }

    fun NotificationPreference.toNotificationPreferenceEntity(): NotificationPreferenceEntity {
        return NotificationPreferenceEntity(
            apiKey = apiKey,
            criticalWarning = criticalWarning
        )
    }

}