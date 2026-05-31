package com.baidaidai.rootless_store.domain.notification.model

data class NotificationPreference(
    val apiKey: String,
    val notificationTitle:String? = null,
    val selfBuiltServer: String? = null,
    val criticalWarning: Boolean,
)