package com.baidaidai.rootless_store.data.notification.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NotificationPreferenceEntity(
    @PrimaryKey
    val apiKey: String,
    val criticalWarning: Boolean
)