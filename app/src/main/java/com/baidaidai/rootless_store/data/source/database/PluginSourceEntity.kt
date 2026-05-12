package com.baidaidai.rootless_store.data.source.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.baidaidai.rootless_store.data.source.mapper.PluginSourceMapper.toPluginSourceAuthenticationMeta
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceInfoDTO
import com.baidaidai.rootless_store.domain.source.model.PluginSourceInfo
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthenticationMeta

@Entity(tableName = "pluginSource")
data class PluginSourceEntity(
    @PrimaryKey
    val sourceID: String,
    val sourceName: String,
    val sourceRemoteEndpoint: String,
    val userAccessToken: String?,

    @Embedded
    val pluginSourceAuthenticationMeta: PluginSourceAuthenticationMeta,

){
    companion object {

        // Work best for Adding PluginSource
        fun fromPluginSourceDTO(
            pluginSourceInfoDTO: PluginSourceInfoDTO
        ): PluginSourceEntity{
            return PluginSourceEntity(
                sourceID = pluginSourceInfoDTO.sourceID,
                sourceName = pluginSourceInfoDTO.sourceName,
                sourceRemoteEndpoint = pluginSourceInfoDTO.sourceRemoteEndpoint,
                userAccessToken = null,
                pluginSourceAuthenticationMeta = pluginSourceInfoDTO.sourceAuthenticationInfo.toPluginSourceAuthenticationMeta(),
            )
        }

        // Work best for Delete PluginSource
        fun fromPluginSourceLocal(
            pluginSourceInfo: PluginSourceInfo
        ): PluginSourceEntity{
            return PluginSourceEntity(
                sourceID = pluginSourceInfo.sourceID,
                sourceName = pluginSourceInfo.sourceName,
                sourceRemoteEndpoint = pluginSourceInfo.sourceRemoteEndpoint,
                userAccessToken = null,
                pluginSourceAuthenticationMeta = pluginSourceInfo.pluginSourceAuthenticationMeta,
            )
        }

    }
}