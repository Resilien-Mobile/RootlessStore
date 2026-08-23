package com.baidaidai.rootless_store.data.source.database

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.baidaidai.rootless_store.data.source.mapper.PluginSourceMapper.toPluginSourceAuthenticationMetadata
import com.baidaidai.rootless_store.data.source.remote.dto.PluginSourceDto
import com.baidaidai.rootless_store.domain.source.model.PluginSource
import com.baidaidai.rootless_store.domain.source.model.PluginSourceAuthenticationMetadata

@Entity(tableName = "pluginSource")
data class PluginSourceEntity(
    @PrimaryKey
    @ColumnInfo(name = "sourceID")
    val sourceId: String,
    val sourceName: String,
    val sourceRemoteEndpoint: String,
    val userAccessToken: String?,

    @Embedded
    val pluginSourceAuthenticationMetadata: PluginSourceAuthenticationMetadata,

){
    companion object {

        // Work best for Adding PluginSource
        fun fromPluginSourceDto(
            pluginSourceDto: PluginSourceDto
        ): PluginSourceEntity{
            return PluginSourceEntity(
                sourceId = pluginSourceDto.sourceId,
                sourceName = pluginSourceDto.sourceName,
                sourceRemoteEndpoint = pluginSourceDto.sourceRemoteEndpoint,
                userAccessToken = null,
                pluginSourceAuthenticationMetadata = pluginSourceDto.authenticationMetadata.toPluginSourceAuthenticationMetadata(),
            )
        }

        // Work best for Delete PluginSource
        fun fromPluginSource(
            pluginSource: PluginSource
        ): PluginSourceEntity{
            return PluginSourceEntity(
                sourceId = pluginSource.sourceId,
                sourceName = pluginSource.sourceName,
                sourceRemoteEndpoint = pluginSource.sourceRemoteEndpoint,
                userAccessToken = null,
                pluginSourceAuthenticationMetadata = pluginSource.pluginSourceAuthenticationMetadata,
            )
        }

    }
}
