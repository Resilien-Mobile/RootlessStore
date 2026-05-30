package com.baidaidai.rootless_store.data.notification.remote.api

import androidx.annotation.IntRange
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import io.ktor.http.appendPathSegments
import javax.inject.Inject

class BarkNotificationAPI @Inject constructor(
    private val ktorClient: HttpClient
) {

    suspend fun pushNormalNotification(
        title: String = "Rootless Store",
        message: String = "你有一个插件异常退出，若非本人操作，请及时前往处理",
        apiKey: String
    ){
        try {
            ktorClient.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.day.app"
                    appendPathSegments(apiKey, title, message)
                }
            }
        }catch (error: Throwable){}
    }

    suspend fun pushWarningNotification(
        title: String = "Rootless Store",
        message: String = "Rootless有一个插件异常退出，若非本人操作，请及时前往处理",
        apiKey: String,
        @IntRange(from = 0, to = 10)
        volume: Int = 5
    ){
        try {
            ktorClient.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.day.app"

                    appendPathSegments(apiKey, title, message)

                    parameters.append("level","critical")
                    parameters.append("volume",volume.toString())

                }
            }
        }catch (error: Throwable){}
    }

}
