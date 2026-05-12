package com.baidaidai.rootless_store.data.source.gateway

import com.baidaidai.rootless_store.data.source.mapper.PluginSourceMapper.toPluginSourceAuthenticationInfo
import com.baidaidai.rootless_store.data.source.mapper.PluginSourceMapper.toPluginSourceInfo
import com.baidaidai.rootless_store.data.source.remote.api.PluginSourceAPI
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceAuthenticationInfoDTO
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceInfoDTO
import com.baidaidai.rootless_store.domain.source.gateway.PluginSourceGateway
import com.baidaidai.rootless_store.domain.source.model.PluginSourceInfo
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthFormInput
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthenticationInfo
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

    suspend fun getPluginSourceAuthenticationInfo(pluginSourceAuthFormInput: PluginSourceAuthFormInput): PluginSourceAuthenticationInfo {
        val ktorResponse = pluginSourceAPI.getPluginSourceAuthenticationInfo(pluginSourceAuthFormInput)
        val pluginSourceAuthenticationInfoDTO = ktorResponse.body<PluginSourceAuthenticationInfoDTO>()  // Convert JSON to DTO

        val pluginSourceAuthenticationInfo = pluginSourceAuthenticationInfoDTO.toPluginSourceAuthenticationInfo()
        return pluginSourceAuthenticationInfo
    }
}