package com.baidaidai.rootless_store.data.notification.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NotificationPreferenceEntity(
    @PrimaryKey
    @ColumnInfo(name = "_primaryKey_")
    val primaryKey: String = "RootlessStoreNotificationPreferenceEntityPrimaryKey",

    val apiKey: String,
    val notificationTitle:String? = null,
    val selfBuiltServer: String? = null,
    @ColumnInfo(name = "criticalWarning")
    val isCriticalWarningEnabled: Boolean,
)
