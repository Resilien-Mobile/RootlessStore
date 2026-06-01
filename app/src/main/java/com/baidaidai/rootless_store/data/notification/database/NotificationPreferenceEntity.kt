package com.baidaidai.rootless_store.data.notification.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NotificationPreferenceEntity(
    @PrimaryKey
    val _primaryKey_: String = "RootlessStoreNotificationPreferenceEntityPrimaryKey",

    val apiKey: String,
    val notificationTitle:String? = null,
    val selfBuiltServer: String? = null,
    val criticalWarning: Boolean,
)