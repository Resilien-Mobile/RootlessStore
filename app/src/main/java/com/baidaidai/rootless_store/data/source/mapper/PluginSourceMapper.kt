package com.baidaidai.rootless_store.data.source.mapper

import com.baidaidai.rootless_store.data.source.database.PluginSourceEntity
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceAuthenticationDto
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceDto
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceAuthenticationMetadataDto
import com.baidaidai.rootless_store.domain.source.model.PluginSource
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthentication
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

    fun PluginSourceAuthenticationDto.toPluginSourceAuthentication(): PluginSourceAuthentication {
        return PluginSourceAuthentication(
            userName = userName,
            userAccessToken = userAccessToken
        )
    }

}
