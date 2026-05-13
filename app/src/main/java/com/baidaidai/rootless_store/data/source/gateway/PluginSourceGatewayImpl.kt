package com.baidaidai.rootless_store.data.source.gateway

import com.baidaidai.rootless_store.data.source.mapper.PluginSourceMapper.toPluginSourceAuthenticationInfo
import com.baidaidai.rootless_store.data.source.mapper.PluginSourceMapper.toPluginSourceInfo
import com.baidaidai.rootless_store.data.source.remote.api.PluginSourceAPI
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceAuthenticationInfoDTO
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceInfoDTO
import com.baidaidai.rootless_store.domain.source.gateway.PluginSourceGateway
import com.baidaidai.rootless_store.domain.source.model.PluginSourceInfo
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthFormInput
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthenticationResult
import io.ktor.client.call.body
import javax.inject.Inject

class PluginSourceGatewayImpl @Inject constructor(
    private val pluginSourceAPI: PluginSourceAPI
): PluginSourceGateway  {

    override suspend fun getPluginSource(sourceRemoteEndpoint: String): PluginSourceInfo {
        val ktorResponse = pluginSourceAPI.getPluginSourceInfo(sourceRemoteEndpoint)
        val pluginSourceInfoDTO = ktorResponse.body<PluginSourceInfoDTO>()  // Convert JSON to DTO

        val pluginSource = pluginSourceInfoDTO.toPluginSourceInfo()
        return pluginSource
    }

    suspend fun getPluginSourceAuthenticationResult(pluginSourceAuthFormInput: PluginSourceAuthFormInput): PluginSourceAuthenticationResult {
        return try {
            val ktorResponse = pluginSourceAPI.getPluginSourceAuthenticationInfo(pluginSourceAuthFormInput)
            val httpStatusCode = ktorResponse.status.value

            when(httpStatusCode){
                200 -> {
                    val pluginSourceAuthenticationInfoDTO = ktorResponse.body<PluginSourceAuthenticationInfoDTO>()  // Convert JSON to DTO
                    val pluginSourceAuthenticationInfo = pluginSourceAuthenticationInfoDTO.toPluginSourceAuthenticationInfo()

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