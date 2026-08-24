package com.baidaidai.rootless_store.data.source.gateway

import com.baidaidai.rootless_store.data.source.mapper.PluginSourceMapper.toPluginSourceCredentials
import com.baidaidai.rootless_store.data.source.mapper.PluginSourceMapper.toPluginSource
import com.baidaidai.rootless_store.data.source.remote.api.PluginSourceApi
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceAuthenticationResponseDto
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceDto
import com.baidaidai.rootless_store.domain.source.gateway.PluginSourceGateway
import com.baidaidai.rootless_store.domain.source.model.PluginSource
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthenticationInput
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

    suspend fun fetchPluginSourceAuthenticationResult(authenticationInput: PluginSourceAuthenticationInput): PluginSourceAuthenticationResult {
        return try {
            val ktorResponse = pluginSourceApi.fetchPluginSourceCredentials(authenticationInput)
            val httpStatusCode = ktorResponse.status.value

            when(httpStatusCode){
                200 -> {
                    val authenticationResponseDto = ktorResponse.body<PluginSourceAuthenticationResponseDto>()  // Convert JSON to DTO
                    val pluginSourceCredentials = authenticationResponseDto.toPluginSourceCredentials()

                    PluginSourceAuthenticationResult.Success(
                        username = pluginSourceCredentials.username,
                        accessToken = pluginSourceCredentials.accessToken
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
