package com.baidaidai.rootless_store.data.notification.gateway

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.baidaidai.rootless_store.data.R
import com.baidaidai.rootless_store.data.notification.remote.api.BarkNotificationApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationGatewayImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val barkNotificationApi: BarkNotificationApi
) {

    fun sendLocalNotification(
        title: String,
        message: String,
        notificationId: Int = 1001
    ){

        val intent = context.packageManager
            .getLaunchIntentForPackage(context.packageName) ?: return

        val pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_IMMUTABLE)

        val channelId = context.getString(R.string.notification_channel_id)

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.material_symbols_directions_run)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat
            .from(context)
            .notify(notificationId, notification)

    }

    suspend fun sendBarkNotification(
        title: String? = null,
        message: String? = null,
        apiKey: String
    ){
        val title = title ?: DEFAULT_NOTIFICATION_TITLE
        val message = message ?: DEFAULT_NOTIFICATION_CONTENT

        barkNotificationApi.sendNotification(
            title = title,
            message = message,
            apiKey = apiKey
        )
    }

    suspend fun sendCriticalBarkNotification(
        title: String? = null,
        message: String? = null,
        apiKey: String
    ){
        val title = title ?: DEFAULT_NOTIFICATION_TITLE
        val message = message ?: DEFAULT_NOTIFICATION_CONTENT

        barkNotificationApi.sendCriticalNotification(
            title = title,
            message = message,
            apiKey = apiKey
        )
    }

    private companion object {
        const val DEFAULT_NOTIFICATION_TITLE = "Rootless Store"
        const val DEFAULT_NOTIFICATION_CONTENT = "你有一个插件异常退出，若非本人操作，请及时前往处理"
    }

}
