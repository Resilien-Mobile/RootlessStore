package com.baidaidai.rootless_store.data.source.mapper

import com.baidaidai.rootless_store.data.source.database.PluginSourceEntity
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceAuthenticationInfoDto
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceInfoDto
import com.baidaidai.rootless_store.data.source.remote.dto.SourceAuthenticationInfoMetaDto
import com.baidaidai.rootless_store.domain.source.model.PluginSourceInfo
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthenticationInfo
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthenticationMeta

object PluginSourceMapper {

    fun SourceAuthenticationInfoMetaDto.toPluginSourceAuthenticationMeta(): PluginSourceAuthenticationMeta {
        return PluginSourceAuthenticationMeta(
            requireAuthentication = requireAuthentication
        )
    }

    fun PluginSourceInfoDto.toPluginSourceInfo(): PluginSourceInfo{
        return PluginSourceInfo(
            sourceId = sourceId,
            sourceName = sourceName,
            sourceRemoteEndpoint = sourceRemoteEndpoint,
            pluginSourceAuthenticationMeta = sourceAuthenticationInfo.toPluginSourceAuthenticationMeta()
        )
    }

    fun PluginSourceEntity.toPluginSourceInfo(): PluginSourceInfo{
        return PluginSourceInfo(
            sourceId = sourceId,
            sourceName = sourceName,
            sourceRemoteEndpoint = sourceRemoteEndpoint,
            pluginSourceAuthenticationMeta = pluginSourceAuthenticationMeta
        )
    }

    fun PluginSourceAuthenticationInfoDto.toPluginSourceAuthenticationInfo(): PluginSourceAuthenticationInfo {
        return PluginSourceAuthenticationInfo(
            userName = userName,
            userAccessToken = userAccessToken
        )
    }

}