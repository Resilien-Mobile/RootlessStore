package com.baidaidai.rootless_store.data.source.remote.api

import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceAuthenticationPostDTO
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthFormInput
import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import javax.inject.Inject

class PluginSourceAPI @Inject constructor(
    private val ktorClient: HttpClient
) {
    private val client = ktorClient
    suspend fun getPluginSourceInfo(
        sourceRemoteEndpoint: String
    ): HttpResponse{
        return client.request(
            urlString = sourceRemoteEndpoint
        ) {
            url {
                appendPathSegments("source", "getSourceInfo")
            }
            accept(ContentType.Application.Json)
        }
    }

    suspend fun getPluginSourceAuthenticationInfo(
        pluginSourceAuthFormInput: PluginSourceAuthFormInput
    ): HttpResponse{
        return client.post(
            urlString = pluginSourceAuthFormInput.sourceRemoteEndpoint
        ) {
            contentType(ContentType.Application.Json)
            url {
                appendPathSegments("source", "auth", "token")
            }

            accept(ContentType.Application.Json)

            setBody(
                PluginSourceAuthenticationPostDTO(
                    userName = pluginSourceAuthFormInput.userName,
                    passWord = pluginSourceAuthFormInput.passWord
                )
            )
        }
    }

}