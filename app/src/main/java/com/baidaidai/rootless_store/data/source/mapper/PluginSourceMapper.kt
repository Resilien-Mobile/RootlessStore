package com.baidaidai.rootless_store.data.source.mapper

import com.baidaidai.rootless_store.data.source.database.PluginSourceEntity
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceAuthenticationInfoDTO
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceInfoDTO
import com.baidaidai.rootless_store.data.source.remote.dto.SourceAuthenticationInfoMetaDTO
import com.baidaidai.rootless_store.domain.source.model.PluginSourceInfo
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthenticationInfo
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthenticationMeta

object PluginSourceMapper {

    fun SourceAuthenticationInfoMetaDTO.toPluginSourceAuthenticationMeta(): PluginSourceAuthenticationMeta {
        return PluginSourceAuthenticationMeta(
            requireAuthentication = requireAuthentication
        )
    }

    fun PluginSourceInfoDTO.toPluginSourceInfo(): PluginSourceInfo{
        return PluginSourceInfo(
            sourceID = sourceID,
            sourceName = sourceName,
            sourceRemoteEndpoint = sourceRemoteEndpoint,
            pluginSourceAuthenticationMeta = sourceAuthenticationInfo.toPluginSourceAuthenticationMeta()
        )
    }

    fun PluginSourceEntity.toPluginSourceInfo(): PluginSourceInfo{
        return PluginSourceInfo(
            sourceID = sourceID,
            sourceName = sourceName,
            sourceRemoteEndpoint = sourceRemoteEndpoint,
            pluginSourceAuthenticationMeta = pluginSourceAuthenticationMeta
        )
    }

    fun PluginSourceAuthenticationInfoDTO.toPluginSourceAuthenticationInfo(): PluginSourceAuthenticationInfo {
        return PluginSourceAuthenticationInfo(
            userName = userName,
            userAccessToken = userAccessToken
        )
    }

}