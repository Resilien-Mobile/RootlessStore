package com.baidaidai.rootless_store.data.source.database

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.baidaidai.rootless_store.data.source.mapper.PluginSourceMapper.toPluginSourceAuthenticationMeta
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceInfoDto
import com.baidaidai.rootless_store.domain.source.model.PluginSourceInfo
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthenticationMeta

@Entity(tableName = "pluginSource")
data class PluginSourceEntity(
    @PrimaryKey
    @ColumnInfo(name = "sourceID")
    val sourceId: String,
    val sourceName: String,
    val sourceRemoteEndpoint: String,
    val userAccessToken: String?,

    @Embedded
    val pluginSourceAuthenticationMeta: PluginSourceAuthenticationMeta,

){
    companion object {

        // Work best for Adding PluginSource
        fun fromPluginSourceDto(
            pluginSourceInfoDto: PluginSourceInfoDto
        ): PluginSourceEntity{
            return PluginSourceEntity(
                sourceId = pluginSourceInfoDto.sourceId,
                sourceName = pluginSourceInfoDto.sourceName,
                sourceRemoteEndpoint = pluginSourceInfoDto.sourceRemoteEndpoint,
                userAccessToken = null,
                pluginSourceAuthenticationMeta = pluginSourceInfoDto.sourceAuthenticationInfo.toPluginSourceAuthenticationMeta(),
            )
        }

        // Work best for Delete PluginSource
        fun fromPluginSourceLocal(
            pluginSourceInfo: PluginSourceInfo
        ): PluginSourceEntity{
            return PluginSourceEntity(
                sourceId = pluginSourceInfo.sourceId,
                sourceName = pluginSourceInfo.sourceName,
                sourceRemoteEndpoint = pluginSourceInfo.sourceRemoteEndpoint,
                userAccessToken = null,
                pluginSourceAuthenticationMeta = pluginSourceInfo.pluginSourceAuthenticationMeta,
            )
        }

    }
}
