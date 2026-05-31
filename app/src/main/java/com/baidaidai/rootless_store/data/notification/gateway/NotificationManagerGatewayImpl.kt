package com.baidaidai.rootless_store.data.notification.gateway

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.baidaidai.rootless_store.MainActivity
import com.baidaidai.rootless_store.R
import com.baidaidai.rootless_store.data.notification.remote.api.BarkNotificationAPI
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationManagerGatewayImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val barkNotificationAPI: BarkNotificationAPI
) {

    fun pushDefaultNotification(
        title: String,
        message: String,
        notificationId: Int = 1001
    ){

        val intent = Intent(context, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_IMMUTABLE)

        val channel_id = context.getString(R.string.notification_channel_id)

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val notification = NotificationCompat.Builder(context, channel_id)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
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

    suspend fun pushNormalNotificationOverBark(
        title: String? = null,
        message: String? = null,
        apiKey: String
    ){
        val title = title ?: defaultNotificationTitle
        val message = message ?: defaultNotificationContent

        barkNotificationAPI.pushNormalNotification(
            title = title,
            message = message,
            apiKey = apiKey
        )
    }

    suspend fun pushWarningNotificationOverBark(
        title: String? = null,
        message: String? = null,
        apiKey: String
    ){
        val title = title ?: defaultNotificationTitle
        val message = message ?: defaultNotificationContent

        barkNotificationAPI.pushWarningNotification(
            title = title,
            message = message,
            apiKey = apiKey
        )
    }

    companion object {

        val defaultNotificationTitle = "Rootless Store"
        val defaultNotificationContent = "你有一个插件异常退出，若非本人操作，请及时前往处理"

    }

}
