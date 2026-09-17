package com.baidaidai.rootless_store.data.notification.mapper

import com.baidaidai.rootless_store.data.notification.database.NotificationPreferenceEntity
import com.baidaidai.rootless_store.data.notification.mapper.NotificationPreferenceMapper.toNotificationPreference
import com.baidaidai.rootless_store.data.notification.mapper.NotificationPreferenceMapper.toNotificationPreferenceEntity
import com.baidaidai.rootless_store.domain.notification.model.NotificationPreference
import org.junit.Assert.assertEquals
import org.junit.Test

class NotificationPreferenceMapperTest {

    @Test
    fun notificationPreferenceEntityToNotificationPreferenceTest() {
        val fakeNotificationPreferenceEntity = NotificationPreferenceEntity(
            apiKey = "bark-api-key",
            notificationTitle = "Rootless Store",
            selfBuiltServer = "https://bark.example.com",
            isCriticalWarningEnabled = true
        )

        val notificationPreference = fakeNotificationPreferenceEntity.toNotificationPreference()

        val expectedNotificationPreference = NotificationPreference(
            apiKey = "bark-api-key",
            notificationTitle = "Rootless Store",
            selfBuiltServer = "https://bark.example.com",
            isCriticalWarningEnabled = true
        )
        assertEquals(expectedNotificationPreference, notificationPreference)
    }

    @Test
    fun notificationPreferenceToNotificationPreferenceEntityTest() {
        val fakeNotificationPreference = NotificationPreference(
            apiKey = "bark-api-key",
            notificationTitle = "Rootless Store",
            selfBuiltServer = "https://bark.example.com",
            isCriticalWarningEnabled = true
        )

        val notificationPreferenceEntity = fakeNotificationPreference.toNotificationPreferenceEntity()

        val expectedNotificationPreferenceEntity = NotificationPreferenceEntity(
            apiKey = "bark-api-key",
            notificationTitle = "Rootless Store",
            selfBuiltServer = "https://bark.example.com",
            isCriticalWarningEnabled = true
        )
        assertEquals(expectedNotificationPreferenceEntity, notificationPreferenceEntity)
    }
}
