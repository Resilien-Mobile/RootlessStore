package com.baidaidai.rootless_store.domain.notification.gateway

interface NotificationGateway {

    fun sendLocalNotification(
        title: String,
        message: String,
        notificationId: Int = 1001
    )

    suspend fun sendBarkNotification(
        title: String? = null,
        message: String? = null,
        apiKey: String
    )

    suspend fun sendCriticalBarkNotification(
        title: String? = null,
        message: String? = null,
        apiKey: String
    )

}
