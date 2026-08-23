package com.baidaidai.rootless_store.data.source.mapper

import com.baidaidai.rootless_store.data.source.database.PluginSourceEntity
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceAuthenticationResponseDto
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceDto
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceAuthenticationMetadataDto
import com.baidaidai.rootless_store.domain.source.model.PluginSource
import com.baidaidai.rootless_store.domain.source.model.PluginSourceCredentials
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthenticationMetadata

object PluginSourceMapper {

    fun PluginSourceAuthenticationMetadataDto.toPluginSourceAuthenticationMetadata(): PluginSourceAuthenticationMetadata {
        return PluginSourceAuthenticationMetadata(
            needsAuthentication = needsAuthentication
        )
    }

    fun PluginSourceDto.toPluginSource(): PluginSource{
        return PluginSource(
            sourceId = sourceId,
            sourceName = sourceName,
            sourceRemoteEndpoint = sourceRemoteEndpoint,
            pluginSourceAuthenticationMetadata = authenticationMetadata.toPluginSourceAuthenticationMetadata()
        )
    }

    fun PluginSourceEntity.toPluginSource(): PluginSource{
        return PluginSource(
            sourceId = sourceId,
            sourceName = sourceName,
            sourceRemoteEndpoint = sourceRemoteEndpoint,
            pluginSourceAuthenticationMetadata = pluginSourceAuthenticationMetadata
        )
    }

    fun PluginSource.toPluginSourceEntity(): PluginSourceEntity {
        return PluginSourceEntity(
            sourceId = sourceId,
            sourceName = sourceName,
            sourceRemoteEndpoint = sourceRemoteEndpoint,
            accessToken = null,
            pluginSourceAuthenticationMetadata = pluginSourceAuthenticationMetadata
        )
    }

    fun PluginSourceAuthenticationResponseDto.toPluginSourceCredentials(): PluginSourceCredentials {
        return PluginSourceCredentials(
            username = username,
            accessToken = accessToken
        )
    }

}
