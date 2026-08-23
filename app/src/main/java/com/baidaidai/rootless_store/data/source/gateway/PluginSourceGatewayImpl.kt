package com.baidaidai.rootless_store.data.source.gateway

import com.baidaidai.rootless_store.data.source.mapper.PluginSourceMapper.toPluginSourceAuthentication
import com.baidaidai.rootless_store.data.source.mapper.PluginSourceMapper.toPluginSource
import com.baidaidai.rootless_store.data.source.remote.api.PluginSourceApi
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceAuthenticationDto
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceDto
import com.baidaidai.rootless_store.domain.source.gateway.PluginSourceGateway
import com.baidaidai.rootless_store.domain.source.model.PluginSource
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthFormInput
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthenticationResult
import io.ktor.client.call.body
import javax.inject.Inject

class PluginSourceGatewayImpl @Inject constructor(
    private val pluginSourceApi: PluginSourceApi
): PluginSourceGateway  {

    override suspend fun fetchPluginSource(sourceRemoteEndpoint: String): PluginSource {
        val ktorResponse = pluginSourceApi.fetchPluginSource(sourceRemoteEndpoint)
        val pluginSourceDto = ktorResponse.body<PluginSourceDto>()  // Convert JSON to DTO

        val pluginSource = pluginSourceDto.toPluginSource()
        return pluginSource
    }

    suspend fun fetchPluginSourceAuthenticationResult(pluginSourceAuthFormInput: PluginSourceAuthFormInput): PluginSourceAuthenticationResult {
        return try {
            val ktorResponse = pluginSourceApi.fetchPluginSourceAuthentication(pluginSourceAuthFormInput)
            val httpStatusCode = ktorResponse.status.value

            when(httpStatusCode){
                200 -> {
                    val pluginSourceAuthenticationDto = ktorResponse.body<PluginSourceAuthenticationDto>()  // Convert JSON to DTO
                    val pluginSourceAuthentication = pluginSourceAuthenticationDto.toPluginSourceAuthentication()

                    PluginSourceAuthenticationResult.Success(
                        userName = pluginSourceAuthentication.userName,
                        userAccessToken = pluginSourceAuthentication.userAccessToken
                    )
                }

                401 -> {
                    PluginSourceAuthenticationResult.AccessDenied(
                        httpStatusCode = httpStatusCode,
                        errorMessage = "The server denied the access"
                    )
                }

                403 -> {
                    PluginSourceAuthenticationResult.AccessDenied(
                        httpStatusCode = httpStatusCode,
                        errorMessage = "Permission denied"
                    )
                }
                else -> { PluginSourceAuthenticationResult.ServerError }
            }
        }catch (error: Throwable){ PluginSourceAuthenticationResult.NetworkError }
    }
}
