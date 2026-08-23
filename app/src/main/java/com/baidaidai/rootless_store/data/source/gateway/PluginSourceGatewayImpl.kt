package com.baidaidai.rootless_store.data.source.gateway

import com.baidaidai.rootless_store.data.source.mapper.PluginSourceMapper.toPluginSourceAuthenticationInfo
import com.baidaidai.rootless_store.data.source.mapper.PluginSourceMapper.toPluginSourceInfo
import com.baidaidai.rootless_store.data.source.remote.api.PluginSourceApi
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceAuthenticationInfoDto
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceInfoDto
import com.baidaidai.rootless_store.domain.source.gateway.PluginSourceGateway
import com.baidaidai.rootless_store.domain.source.model.PluginSourceInfo
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthFormInput
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthenticationResult
import io.ktor.client.call.body
import javax.inject.Inject

class PluginSourceGatewayImpl @Inject constructor(
    private val pluginSourceApi: PluginSourceApi
): PluginSourceGateway  {

    override suspend fun fetchPluginSource(sourceRemoteEndpoint: String): PluginSourceInfo {
        val ktorResponse = pluginSourceApi.fetchPluginSourceInfo(sourceRemoteEndpoint)
        val pluginSourceInfoDto = ktorResponse.body<PluginSourceInfoDto>()  // Convert JSON to DTO

        val pluginSource = pluginSourceInfoDto.toPluginSourceInfo()
        return pluginSource
    }

    suspend fun fetchPluginSourceAuthenticationResult(pluginSourceAuthFormInput: PluginSourceAuthFormInput): PluginSourceAuthenticationResult {
        return try {
            val ktorResponse = pluginSourceApi.fetchPluginSourceAuthenticationInfo(pluginSourceAuthFormInput)
            val httpStatusCode = ktorResponse.status.value

            when(httpStatusCode){
                200 -> {
                    val pluginSourceAuthenticationInfoDto = ktorResponse.body<PluginSourceAuthenticationInfoDto>()  // Convert JSON to DTO
                    val pluginSourceAuthenticationInfo = pluginSourceAuthenticationInfoDto.toPluginSourceAuthenticationInfo()

                    PluginSourceAuthenticationResult.Success(
                        userName = pluginSourceAuthenticationInfo.userName,
                        userAccessToken = pluginSourceAuthenticationInfo.userAccessToken
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
